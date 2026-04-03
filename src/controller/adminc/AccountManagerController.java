package controller.adminc;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.nhethong.NguoiDung;
import dao.nhethongdao.NguoiDungDAO;

import java.util.Optional;

public class AccountManagerController {

    @FXML private TableView<NguoiDung> tableUsers;
    @FXML private TableColumn<NguoiDung, String> colUsername, colFullName, colType, colMaSV, colMaNV;
    @FXML private TextField txtSearch;

    private ObservableList<NguoiDung> userList = FXCollections.observableArrayList();
    private FilteredList<NguoiDung> filteredData;
    private NguoiDungDAO dao = new NguoiDungDAO();

    @FXML
    public void initialize() {
        // 1. Ánh xạ dữ liệu vào các cột (Đã loại bỏ colActions để tránh lỗi Null)
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colType.setCellValueFactory(new PropertyValueFactory<>("usertype"));
        colMaSV.setCellValueFactory(new PropertyValueFactory<>("maSV"));
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNV"));

        // 2. Tải dữ liệu ban đầu
        loadData();

        // 3. Thiết lập tìm kiếm nhanh (Live Search)
        setupSearch();
    }

    private void loadData() {
        // Lấy danh sách tài khoản đang hoạt động (isDeleted = 0) từ DAO
        userList = dao.getAllNguoiDung();
        filteredData = new FilteredList<>(userList, p -> true);
        tableUsers.setItems(filteredData);
    }

    private void setupSearch() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();

                if (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerCaseFilter)) return true;
                if (user.getMaSV() != null && user.getMaSV().toLowerCase().contains(lowerCaseFilter)) return true;
                if (user.getMaNV() != null && user.getMaNV().toLowerCase().contains(lowerCaseFilter)) return true;
                return false;
            });
        });
    }

    @FXML
    private void handleSearch() {
        loadData(); // Làm mới dữ liệu từ Database
    }

    // --- CÁC HÀM XỬ LÝ TRONG MENU TÁC VỤ ---

    @FXML
    private void handleDeleteSelectedAccount() {
        // Lấy dòng đang được chọn trên TableView
        NguoiDung selected = tableUsers.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Cảnh báo", "Vui lòng chọn một tài khoản từ danh sách để xóa!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xác nhận xóa tạm thời");
        confirm.setHeaderText("Xóa tài khoản: " + selected.getUsername() + "?");
        confirm.setContentText("Lưu ý: Trạng thái sinh viên sẽ chuyển thành 'Thôi học'.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dao.deleteNguoiDung(selected.getUsername());
            loadData(); // Cập nhật lại bảng
            showAlert("Thành công", "Tài khoản đã được chuyển vào thùng rác.");
        }
    }

    @FXML
    private void handleShowDeleteHistory() {
        ObservableList<NguoiDung> deletedList = dao.getAllDeletedNguoiDung();

        if (deletedList.isEmpty()) {
            showAlert("Thông báo", "Thùng rác hiện đang trống.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Lịch sử xóa - Thùng rác");
        dialog.setHeaderText("Danh sách tài khoản có thể khôi phục");

        // --- 1. THÊM THANH TÌM KIẾM ---
        TextField txtSearchTrash = new TextField();
        txtSearchTrash.setPromptText("Nhập tên tài khoản để tìm kiếm...");
        txtSearchTrash.setStyle("-fx-background-radius: 15; -fx-padding: 5 10;");

        // Khởi tạo TableView
        TableView<NguoiDung> tableDeleted = new TableView<>();

        // --- 2. BỌC DANH SÁCH VÀO FILTERED LIST ĐỂ TÌM KIẾM TRỰC TIẾP ---
        FilteredList<NguoiDung> filteredTrash = new FilteredList<>(deletedList, p -> true);
        tableDeleted.setItems(filteredTrash);

        // Lắng nghe sự kiện gõ phím để lọc
        txtSearchTrash.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredTrash.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();

                // Tìm kiếm theo username (hoặc có thể thêm các trường khác nếu muốn)
                return user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // 3. THIẾT LẬP CỘT CHO BẢNG
        TableColumn<NguoiDung, String> colUser = new TableColumn<>("Tên tài khoản");
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUser.setPrefWidth(200);

        TableColumn<NguoiDung, String> colRes = new TableColumn<>("Thao tác");
        colRes.setCellFactory(p -> new TableCell<>() {
            private final Button btnRestore = new Button("Khôi phục");
            {
                btnRestore.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");
                btnRestore.setOnAction(e -> {
                    // Lấy đối tượng từ danh sách đã lọc thay vì getItems() gốc
                    NguoiDung nd = getTableView().getItems().get(getIndex());
                    if (dao.restoreNguoiDung(nd.getUsername())) {
                        deletedList.remove(nd); // Xóa khỏi danh sách gốc, bảng sẽ tự cập nhật
                        loadData(); // Cập nhật lại bảng chính ở phía sau

                        // Đóng form nếu thùng rác trống sau khi khôi phục
                        if (deletedList.isEmpty()) {
                            dialog.setResult(null);
                            dialog.close();
                            showAlert("Thông báo", "Thùng rác đã trống.");
                        }
                    }
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnRestore);
            }
        });

        tableDeleted.getColumns().addAll(colUser, colRes);
        tableDeleted.setPrefHeight(300);

        // --- 4. GỘP THANH TÌM KIẾM VÀ BẢNG VÀO VBOX ---
        VBox content = new VBox(10, txtSearchTrash, tableDeleted); // Khoảng cách 10px

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    @FXML
    private void handleCreateAllAccounts() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Khởi tạo tự động cho tất cả SV/NV chưa có tài khoản?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                int count = dao.createAccountsForAll();
                if (count > 0) {
                    showAlert("Thành công", "Đã tạo thêm " + count + " tài khoản.");
                    loadData();
                } else {
                    showAlert("Thông báo", "Không có tài khoản mới nào cần tạo.");
                }
            }
        });
    }

    @FXML
    private void handleCreateSingleAccountManual() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Tạo tài khoản lẻ");
        dialog.setHeaderText("Nhập mã SV/NV cần cấp quyền:");
        dialog.showAndWait().ifPresent(id -> {
            if (dao.findByUsername(id) != null) {
                showAlert("Lỗi", "Tài khoản đã tồn tại!");
            } else {
                if (dao.createSingleAccount(id)) {
                    loadData();
                    showAlert("Thành công", "Đã tạo tài khoản cho: " + id);
                } else {
                    showAlert("Lỗi", "Không tìm thấy mã này trong hồ sơ.");
                }
            }
        });
    }

    @FXML
    private void handleResetAllPasswords() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Đưa TOÀN BỘ mật khẩu về ngày sinh?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                int count = dao.resetAllPasswordsToDefault();
                showAlert("Thành công", "Đã reset " + count + " tài khoản.");
            }
        });
    }

    @FXML
    private void handleResetSelectedPassword() {
        NguoiDung selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Cảnh báo", "Vui lòng chọn 1 dòng!");
            return;
        }
        String type = (selected.getMaSV() != null) ? "SV" : "NV";
        if (dao.resetPasswordToBirthdate(selected.getUsername(), type)) {
            showAlert("Thành công", "Đã reset mật khẩu cho " + selected.getUsername());
        }
    }

    @FXML
    private void handleQuickUpdateRole() {
        NguoiDung selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn tài khoản cần sửa quyền!");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(selected.getUsertype(), "AD", "GV", "SV", "NV");
        dialog.setTitle("Phân quyền");
        dialog.setHeaderText("Cập nhật vai trò cho: " + selected.getUsername());

        dialog.showAndWait().ifPresent(newRole -> {
            selected.setUsertype(newRole);
            if (newRole.equals("AD")) {
                selected.setMaSV(null);
                selected.setMaNV(null);
            } else if (newRole.equals("SV")) {
                selected.setMaNV(null);
            } else {
                selected.setMaSV(null);
            }

            dao.updateNguoiDung(selected);
            tableUsers.refresh();
            showAlert("Thành công", "Đã cập nhật quyền thành: " + newRole);
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}