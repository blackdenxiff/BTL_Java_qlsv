package controller.nhanvien;

import dao.quanlydao.DangKiDAO;
import model.quanly.DangKi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser; // Cần import để mở cửa sổ lưu file

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class NhapDiemController {

    private DangKiDAO dkDAO = new DangKiDAO();

    @FXML private TextField txtMaLHP;
    @FXML private TextField txtMaSV;
    @FXML private TextField txtDiem;

    @FXML private TableView<Map<String, Object>> tableDiem;
    @FXML private TableColumn<Map<String, Object>, String> colMaSV;
    @FXML private TableColumn<Map<String, Object>, String> colHoTen;
    @FXML private TableColumn<Map<String, Object>, Double> colDiem;
    @FXML private TableColumn<Map<String, Object>, String> colTrangThai;

    /**
     * Load bảng điểm
     */
    @FXML
    public void handleLoad() {
        String maLHP = txtMaLHP.getText().trim();

        if (maLHP.isEmpty()) {
            showAlert("Vui lòng nhập Mã lớp học phần để tải danh sách!");
            return;
        }

        List<Map<String, Object>> list = dkDAO.getBangDiemByLHP(maLHP);

        if (list == null || list.isEmpty()) {
            showAlert("Không tìm thấy danh sách lớp hoặc lớp chưa có sinh viên!");
            tableDiem.getItems().clear(); // Xóa bảng nếu không có dữ liệu
            return;
        }

        ObservableList<Map<String, Object>> data = FXCollections.observableArrayList(list);

        colMaSV.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().get("maSV").toString()));
        colHoTen.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().get("hoTen").toString()));
        colDiem.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>((Double) cell.getValue().get("diemSo")));
        colTrangThai.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().get("trangThai").toString()));

        tableDiem.setItems(data);
    }

    /**
     * Lưu điểm
     */
    @FXML
    public void handleSave() {
        String maLHP = txtMaLHP.getText().trim();
        String maSV = txtMaSV.getText().trim();

        // kiểm tra rỗng
        if (maLHP.isEmpty() || maSV.isEmpty() || txtDiem.getText().isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        double diem;
        try {
            diem = Double.parseDouble(txtDiem.getText().trim());
            if (diem < 0 || diem > 10) {
                showAlert("Điểm phải nằm trong khoảng từ 0 đến 10!");
                return;
            }
        } catch (Exception e) {
            showAlert("Điểm phải là số hợp lệ!");
            return;
        }

        // 🔥 kiểm tra mã SV có tồn tại trong lớp không
        List<Map<String, Object>> list = dkDAO.getBangDiemByLHP(maLHP);
        boolean tonTai = false;

        for (Map<String, Object> row : list) {
            if (row.get("maSV").toString().equals(maSV)) {
                tonTai = true;
                break;
            }
        }

        if (!tonTai) {
            showAlert("Không tìm thấy mã sinh viên " + maSV + " trong lớp này!");
            return;
        }

        // lưu điểm
        String trangThai = (diem >= 4.0) ? "Qua môn" : "Học lại";

        DangKi dk = new DangKi(maSV, maLHP, diem, trangThai);
        dkDAO.updateDangKy(dk);

        showAlert("Lưu điểm thành công!");

        handleLoad(); // reload bảng để hiển thị điểm mới

        // Tự động xóa ô nhập để nhập người tiếp theo
        txtMaSV.clear();
        txtDiem.clear();
    }

    /**
     * Xuất dữ liệu bảng điểm ra file Excel (CSV)
     */
    @FXML
    public void handleExport() {
        ObservableList<Map<String, Object>> danhSach = tableDiem.getItems();
        if (danhSach == null || danhSach.isEmpty()) {
            showAlert("Bảng điểm đang trống, vui lòng Tải danh sách trước khi xuất!");
            return;
        }

        String maLHP = txtMaLHP.getText().trim();
        String tenFileMacDinh = maLHP.isEmpty() ? "DanhSachDiem.csv" : "BangDiem_" + maLHP + ".csv";

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file bảng điểm (CSV)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName(tenFileMacDinh);
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                // Thêm ký tự BOM để Excel đọc chuẩn Tiếng Việt
                bw.write('\ufeff');
                bw.write("Mã SV,Họ và tên,Điểm,Trạng thái\n");

                for (Map<String, Object> row : danhSach) {
                    // Lấy dữ liệu an toàn
                    String maSV = row.get("maSV") != null ? row.get("maSV").toString() : "";
                    String hoTen = row.get("hoTen") != null ? row.get("hoTen").toString() : "";
                    String diem = row.get("diemSo") != null ? row.get("diemSo").toString() : "";
                    String trangThai = row.get("trangThai") != null ? row.get("trangThai").toString() : "";

                    // Thủ thuật: Thêm \t để Mã SV hiển thị chuẩn trong Excel
                    String maSV_Format = "\t" + maSV;

                    String line = String.format("%s,%s,%s,%s\n", maSV_Format, hoTen, diem, trangThai);
                    bw.write(line);
                }

                showAlert("Đã xuất bảng điểm ra file thành công!");
            } catch (Exception e) {
                showAlert("Có lỗi xảy ra khi lưu file: " + e.getMessage());
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait(); // Nên dùng showAndWait để buộc người dùng đọc thông báo
    }
}