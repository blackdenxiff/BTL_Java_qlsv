package controller.nhanvien;

import dao.quanlydao.DangKiDAO;
import model.quanly.DangKi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
        String maLHP = txtMaLHP.getText();

        List<Map<String, Object>> list = dkDAO.getBangDiemByLHP(maLHP);

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
        String maLHP = txtMaLHP.getText();
        String maSV = txtMaSV.getText();

        // kiểm tra rỗng
        if (maLHP.isEmpty() || maSV.isEmpty() || txtDiem.getText().isEmpty()) {
            showAlert("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        double diem;
        try {
            diem = Double.parseDouble(txtDiem.getText());
        } catch (Exception e) {
            showAlert("Điểm phải là số!");
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
            showAlert("Không tìm thấy mã sinh viên!");
            return;
        }

        // lưu điểm
        String trangThai = (diem >= 4.0) ? "Qua môn" : "Học lại";

        DangKi dk = new DangKi(maSV, maLHP, diem, trangThai);
        dkDAO.updateDangKy(dk);

        showAlert("Lưu điểm thành công!");

        handleLoad(); // reload bảng
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}