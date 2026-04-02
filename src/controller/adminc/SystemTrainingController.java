package controller.adminc;

import database.KNDatabase;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SystemTrainingController {
    @FXML private TreeView<String> treeNavigation;
    @FXML private TableView<ObservableList<String>> tableDetails;
    @FXML private TextField txtSearch;

    private FilteredList<ObservableList<String>> filteredData;
    private String currentTableType = ""; // Xác định bảng đang hiển thị: KHOA, CTDT, LHC, MH, LHP
    private String currentParentId = "";  // ID của node cha đang được chọn trên cây
    private List<String> currentColumnNames = new ArrayList<>(); // Lưu tên cột database để mapping

    @FXML
    public void initialize() {
        buildTreeView();
        setupLiveSearch();
        setupTableDoubleClickListener();

        // Lắng nghe sự kiện click trên cây thư mục
        treeNavigation.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) handleTreeSelection(newVal);
        });
    }

    // ================= 1. XÂY DỰNG CÂY THƯ MỤC (Dựa theo ERD) =================
    private void buildTreeView() {
        TreeItem<String> root = new TreeItem<>("TRƯỜNG ĐẠI HỌC GTVT");
        root.setExpanded(true);

        String sqlKhoa = "SELECT MaKhoa, TenKhoa FROM Khoa WHERE (isDeleted = 0 OR isDeleted IS NULL)";
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlKhoa);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String maKhoa = rs.getString("MaKhoa");
                TreeItem<String> nodeKhoa = new TreeItem<>("Khoa: " + rs.getString("TenKhoa") + " [" + maKhoa + "]");

                TreeItem<String> nodeCTDT = new TreeItem<>("Danh mục CTĐT [" + maKhoa + "]");
                TreeItem<String> nodeMH = new TreeItem<>("Danh mục Môn học [" + maKhoa + "]");

                nodeKhoa.getChildren().addAll(nodeCTDT, nodeMH);
                root.getChildren().add(nodeKhoa);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        treeNavigation.setRoot(root);
    }

    private void handleTreeSelection(TreeItem<String> item) {
        String val = item.getValue();
        if (val.equals("TRƯỜNG ĐẠI HỌC GTVT")) {
            currentParentId = "";
            loadTableData("KHOA", "SELECT MaKhoa, TenKhoa FROM Khoa WHERE isDeleted = 0", null, "MaKhoa", "TenKhoa");
        }
        else if (val.startsWith("Danh mục CTĐT")) {
            currentParentId = extractId(val);
            loadTableData("CTDT", "SELECT MaCTDT, TenCTDT, TongTinChi, NamApDung FROM ChuongTrinhDaoTao WHERE MaKhoa = ? AND isDeleted = 0", currentParentId, "MaCTDT", "TenCTDT", "TongTinChi", "NamApDung");
        }
        else if (val.startsWith("Danh mục Môn học")) {
            currentParentId = extractId(val);
            loadTableData("MH", "SELECT MaMH, TenMH, SoTC, LoaiMon FROM MonHoc WHERE MaCTDT IN (SELECT MaCTDT FROM ChuongTrinhDaoTao WHERE MaKhoa = ?) AND isDeleted = 0", currentParentId, "MaMH", "TenMH", "SoTC", "LoaiMon");
        }
    }

    private String extractId(String val) {
        return val.substring(val.indexOf("[") + 1, val.indexOf("]"));
    }

    // ================= 2. QUẢN LÝ DỮ LIỆU TABLEVIEW =================
    private void loadTableData(String type, String sql, String param, String... colNames) {
        this.currentTableType = type;
        this.currentColumnNames = List.of(colNames);
        tableDetails.getColumns().clear();
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

        // Tạo cột giao diện
        for (int i = 0; i < colNames.length; i++) {
            final int colIdx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(colNames[i]);
            col.setCellValueFactory(paramData -> new SimpleStringProperty(paramData.getValue().get(colIdx)));
            tableDetails.getColumns().add(col);
        }

        // Lấy dữ liệu
        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (param != null) ps.setString(1, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (String colName : colNames) {
                    row.add(rs.getString(colName));
                }
                data.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        filteredData = new FilteredList<>(data, p -> true);
        tableDetails.setItems(filteredData);
    }

    private void setupTableDoubleClickListener() {
        tableDetails.setRowFactory(tv -> {
            TableRow<ObservableList<String>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ObservableList<String> rowData = row.getItem();
                    String id = rowData.get(0); // Lấy ID của dòng click đúp
                    currentParentId = id; // Cập nhật node cha hiện tại

                    if ("CTDT".equals(currentTableType)) {
                        String sql = "SELECT MaLop, TenLop, NienKhoa FROM LopHanhChinh WHERE MaCTDT = ? AND (isDeleted = 0 OR isDeleted IS NULL)";
                        loadTableData("LHC", sql, id, "Mã Lớp", "Tên Lớp", "Niên Khóa");
                    }
                    else if ("LHC".equals(currentTableType)) {
                        String sql = "SELECT MaSV, HoDem, Ten, NgaySinh, GioiTinh, TrangThaiSinhSV FROM SinhVien WHERE MaLop = ? AND (isDeleted = 0 OR isDeleted IS NULL)";
                        loadTableData("SINHVIEN", sql, id, "Mã SV", "Họ Đệm", "Tên", "Ngày Sinh", "Giới Tính", "Trạng Thái");
                    }
                    else if ("MH".equals(currentTableType)) {
                        String sql = "SELECT MaLHP, NamHoc, HocKy, GioiHanSV FROM LopHocPhan WHERE MaMH = ? AND (isDeleted = 0 OR isDeleted IS NULL)";
                        loadTableData("LHP", sql, id, "Mã LHP", "Năm Học", "Học Kỳ", "Giới Hạn SV");
                    }
                }
            });
            return row;
        });
    }

    // ================= 3. TÌM KIẾM (LIVE SEARCH) =================
    private void setupLiveSearch() {
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            if (filteredData != null) {
                filteredData.setPredicate(row -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    String lowerCaseFilter = newVal.toLowerCase();
                    for (String cell : row) {
                        if (cell != null && cell.toLowerCase().contains(lowerCaseFilter)) return true;
                    }
                    return false;
                });
            }
        });
    }

    // ================= 4. THÊM VÀ SỬA (DYNAMIC FORM) =================
    @FXML
    private void handleAdd() {
        if (currentTableType.isEmpty() || currentTableType.equals("SINHVIEN")) {
            showAlert("Thông báo", "Vui lòng chọn Khoa, CTĐT hoặc Môn học trên bảng để thêm dữ liệu mới!");
            return;
        }
        showDynamicFormDialog(false, null);
    }

    @FXML
    private void handleEdit() {
        ObservableList<String> selected = tableDetails.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Cảnh báo", "Vui lòng chọn một dòng trên bảng để sửa!"); return;
        }
        showDynamicFormDialog(true, selected);
    }

    private void showDynamicFormDialog(boolean isEdit, ObservableList<String> rowData) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isEdit ? "Sửa thông tin" : "Thêm mới " + currentTableType);
        dialog.setHeaderText("Nhập thông tin cho " + currentTableType);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 100, 10, 10));

        List<TextField> inputs = new ArrayList<>();

        for (int i = 0; i < currentColumnNames.size(); i++) {
            grid.add(new Label(currentColumnNames.get(i) + ":"), 0, i);
            TextField txt = new TextField();

            if (isEdit) {
                txt.setText(rowData.get(i));
                if (i == 0) txt.setDisable(true); // Không cho sửa Khóa chính (Mã)
            }
            inputs.add(txt);
            grid.add(txt, 1, i);
        }

        dialog.getDialogPane().setContent(grid);
        ButtonType btnSave = new ButtonType("Lưu dữ liệu", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnSave, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnSave) {
                List<String> values = new ArrayList<>();
                for (TextField txt : inputs) values.add(txt.getText());
                saveDataToDB(isEdit, values);
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void saveDataToDB(boolean isEdit, List<String> values) {
        String sql = "";
        try (Connection conn = KNDatabase.getConnection()) {
            PreparedStatement ps = null;

            if (currentTableType.equals("KHOA")) {
                if (isEdit) sql = "UPDATE Khoa SET TenKhoa = ? WHERE MaKhoa = ?";
                else sql = "INSERT INTO Khoa (MaKhoa, TenKhoa, isDeleted) VALUES (?, ?, 0)";
                ps = conn.prepareStatement(sql);
                if (isEdit) { ps.setString(1, values.get(1)); ps.setString(2, values.get(0)); }
                else { ps.setString(1, values.get(0)); ps.setString(2, values.get(1)); }
            }
            else if (currentTableType.equals("CTDT")) {
                if (isEdit) sql = "UPDATE ChuongTrinhDaoTao SET TenCTDT = ?, TongTinChi = ?, NamApDung = ? WHERE MaCTDT = ?";
                else sql = "INSERT INTO ChuongTrinhDaoTao (MaCTDT, TenCTDT, TongTinChi, NamApDung, MaKhoa, isDeleted) VALUES (?, ?, ?, ?, ?, 0)";
                ps = conn.prepareStatement(sql);
                if (isEdit) {
                    ps.setString(1, values.get(1)); ps.setInt(2, Integer.parseInt(values.get(2)));
                    ps.setInt(3, Integer.parseInt(values.get(3))); ps.setString(4, values.get(0));
                } else {
                    ps.setString(1, values.get(0)); ps.setString(2, values.get(1));
                    ps.setInt(3, Integer.parseInt(values.get(2))); ps.setInt(4, Integer.parseInt(values.get(3)));
                    ps.setString(5, currentParentId);
                }
            }
            else if (currentTableType.equals("LHC")) {
                if (isEdit) sql = "UPDATE LopHanhChinh SET TenLop = ?, NienKhoa = ? WHERE MaLop = ?";
                else sql = "INSERT INTO LopHanhChinh (MaLop, TenLop, NienKhoa, MaCTDT, isDeleted) VALUES (?, ?, ?, ?, 0)";
                ps = conn.prepareStatement(sql);
                if (isEdit) {
                    ps.setString(1, values.get(1)); ps.setString(2, values.get(2)); ps.setString(3, values.get(0));
                } else {
                    ps.setString(1, values.get(0)); ps.setString(2, values.get(1));
                    ps.setString(3, values.get(2)); ps.setString(4, currentParentId);
                }
            }
            else if (currentTableType.equals("MH")) {
                if (isEdit) sql = "UPDATE MonHoc SET TenMH = ?, SoTC = ?, LoaiMon = ? WHERE MaMH = ?";
                else sql = "INSERT INTO MonHoc (MaMH, TenMH, SoTC, LoaiMon, MaCTDT, isDeleted) VALUES (?, ?, ?, ?, ?, 0)";
                ps = conn.prepareStatement(sql);
                if (isEdit) {
                    ps.setString(1, values.get(1)); ps.setInt(2, Integer.parseInt(values.get(2)));
                    ps.setString(3, values.get(3)); ps.setString(4, values.get(0));
                } else {
                    ps.setString(1, values.get(0)); ps.setString(2, values.get(1));
                    ps.setInt(3, Integer.parseInt(values.get(2))); ps.setString(4, values.get(3));
                    ps.setString(5, currentParentId);
                }
            }

            if (ps != null) {
                ps.executeUpdate();
                showAlert("Thành công", isEdit ? "Cập nhật thành công!" : "Thêm mới thành công!");

                // Refresh lại Table và Tree
                TreeItem<String> selectedItem = treeNavigation.getSelectionModel().getSelectedItem();
                if (selectedItem != null) handleTreeSelection(selectedItem);
                if (!isEdit && currentTableType.equals("KHOA")) buildTreeView();
            }
        } catch (SQLException e) {
            showAlert("Lỗi CSDL", "Có lỗi xảy ra (Có thể trùng Mã): " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Lỗi Nhập Liệu", "Vui lòng nhập đúng định dạng SỐ cho các trường Tín chỉ, Năm áp dụng...");
        }
    }

    // ================= 5. XÓA VÀ KHÔI PHỤC DÂY CHUYỀN (CASCADING) =================
    @FXML
    private void handleDelete() {
        ObservableList<String> selected = tableDetails.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Cảnh báo", "Vui lòng chọn một dòng để xóa!"); return;
        }

        String id = selected.get(0);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Xóa " + id + " sẽ ẨN TOÀN BỘ dữ liệu liên quan bên dưới. Tiếp tục?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                if (executeCascadeSoftDelete(currentTableType, id, 1)) {
                    showAlert("Thành công", "Đã chuyển " + id + " vào thùng rác.");
                    buildTreeView();
                    tableDetails.getItems().remove(selected);
                }
            }
        });
    }


    @FXML
    private void handleRestore() {
        if (currentTableType.isEmpty()) {
            showAlert("Thông báo", "Vui lòng chọn một mục (Khoa, CTĐT...) bên trái để xem thùng rác!");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Thùng rác - " + currentTableType);
        dialog.setHeaderText("Danh sách dữ liệu đã xóa (Có thể khôi phục)");
        dialog.getDialogPane().setPrefSize(700, 400);

        TextField txtSearchTrash = new TextField();
        txtSearchTrash.setPromptText("Tìm kiếm trong thùng rác...");
        txtSearchTrash.setStyle("-fx-background-radius: 15;");

        TableView<ObservableList<String>> tableTrash = new TableView<>();

        // 1. Tạo các cột dữ liệu
        for (int i = 0; i < currentColumnNames.size(); i++) {
            final int colIdx = i;
            TableColumn<ObservableList<String>, String> col = new TableColumn<>(currentColumnNames.get(i));
            col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colIdx)));
            tableTrash.getColumns().add(col);
        }

        // 2. LẤY DỮ LIỆU GỐC TRƯỚC (Đây là mấu chốt để sửa lỗi)
        ObservableList<ObservableList<String>> trashData = loadTrashDataFromDB();
        FilteredList<ObservableList<String>> filteredTrash = new FilteredList<>(trashData, p -> true);
        tableTrash.setItems(filteredTrash);

        // 3. Tạo cột thao tác "Khôi phục"
        TableColumn<ObservableList<String>, Void> colAction = new TableColumn<>("Thao tác");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Khôi phục");
            {
                btn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");
                btn.setOnAction(e -> {
                    ObservableList<String> row = getTableView().getItems().get(getIndex());
                    String id = row.get(0);

                    if (executeCascadeSoftDelete(currentTableType, id, 0)) { // 0 là khôi phục
                        // SỬA LỖI Ở ĐÂY: Xóa phần tử khỏi danh sách gốc 'trashData'
                        trashData.remove(row);

                        TreeItem<String> selectedItem = treeNavigation.getSelectionModel().getSelectedItem();
                        if (selectedItem != null) handleTreeSelection(selectedItem);
                        buildTreeView();

                        // HIỆN THÔNG BÁO THÀNH CÔNG
                        showAlert("Khôi phục thành công", "Bạn đã khôi phục thành công dữ liệu mã: " + id);
                    } else {
                        showAlert("Thất bại", "Khôi phục không thành công do lỗi cơ sở dữ liệu!");
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        tableTrash.getColumns().add(colAction);

        // 4. Logic tìm kiếm (Live Search)
        txtSearchTrash.textProperty().addListener((obs, oldV, newV) -> {
            filteredTrash.setPredicate(row -> {
                if (newV == null || newV.isEmpty()) return true;
                String lower = newV.toLowerCase();
                for (String cell : row) {
                    if (cell != null && cell.toLowerCase().contains(lower)) return true;
                }
                return false;
            });
        });

        VBox content = new VBox(10, txtSearchTrash, tableTrash);
        VBox.setVgrow(tableTrash, Priority.ALWAYS);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private ObservableList<ObservableList<String>> loadTrashDataFromDB() {
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        String sql = "";

        if (currentTableType.equals("KHOA")) sql = "SELECT MaKhoa, TenKhoa FROM Khoa WHERE isDeleted = 1";
        else if (currentTableType.equals("CTDT")) sql = "SELECT MaCTDT, TenCTDT, TongTinChi, NamApDung FROM ChuongTrinhDaoTao WHERE MaKhoa = ? AND isDeleted = 1";
        else if (currentTableType.equals("LHC")) sql = "SELECT MaLop, TenLop, NienKhoa FROM LopHanhChinh WHERE MaCTDT = ? AND isDeleted = 1";
        else if (currentTableType.equals("MH")) sql = "SELECT MaMH, TenMH, SoTC, LoaiMon FROM MonHoc WHERE MaCTDT IN (SELECT MaCTDT FROM ChuongTrinhDaoTao WHERE MaKhoa = ?) AND isDeleted = 1";
        else return data;

        try (Connection conn = KNDatabase.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (!currentTableType.equals("KHOA") && currentParentId != null && !currentParentId.isEmpty()) {
                ps.setString(1, currentParentId);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (String colName : currentColumnNames) {
                    row.add(rs.getString(colName));
                }
                data.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    private boolean executeCascadeSoftDelete(String type, String id, int status) {
        Connection conn = null;
        try {
            conn = KNDatabase.getConnection();
            conn.setAutoCommit(false);

            if (type.equals("KHOA")) {
                executeUpdate(conn, "UPDATE Khoa SET isDeleted = ? WHERE MaKhoa = ?", status, id);
                executeUpdate(conn, "UPDATE ChuongTrinhDaoTao SET isDeleted = ? WHERE MaKhoa = ?", status, id);
                executeUpdate(conn, "UPDATE LopHanhChinh SET isDeleted = ? WHERE MaCTDT IN (SELECT MaCTDT FROM ChuongTrinhDaoTao WHERE MaKhoa = ?)", status, id);
                executeUpdate(conn, "UPDATE MonHoc SET isDeleted = ? WHERE MaCTDT IN (SELECT MaCTDT FROM ChuongTrinhDaoTao WHERE MaKhoa = ?)", status, id);
            }
            else if (type.equals("CTDT")) {
                executeUpdate(conn, "UPDATE ChuongTrinhDaoTao SET isDeleted = ? WHERE MaCTDT = ?", status, id);
                executeUpdate(conn, "UPDATE LopHanhChinh SET isDeleted = ? WHERE MaCTDT = ?", status, id);
            }
            else if (type.equals("LHC")) {
                executeUpdate(conn, "UPDATE LopHanhChinh SET isDeleted = ? WHERE MaLop = ?", status, id);
                executeUpdate(conn, "UPDATE SinhVien SET isDeleted = ? WHERE MaLop = ?", status, id);
            }
            else if (type.equals("MH")) {
                executeUpdate(conn, "UPDATE MonHoc SET isDeleted = ? WHERE MaMH = ?", status, id);
                executeUpdate(conn, "UPDATE LopHocPhan SET isDeleted = ? WHERE MaMH = ?", status, id);
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

    private void executeUpdate(Connection conn, String sql, int status, String id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}