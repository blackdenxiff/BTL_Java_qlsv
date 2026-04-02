package controller.nhanvien;

import dao.nconnguoidao.SinhVienDAO;
import model.nconnguoi.SinhVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class UpdateSinhVien {

    private SinhVienDAO svDAO = new SinhVienDAO();

    // TABLE
    @FXML private TableView<SinhVien> tableSinhVien;
    @FXML private TableColumn<SinhVien, String> colMaSV;
    @FXML private TableColumn<SinhVien, String> colHoTen;
    @FXML private TableColumn<SinhVien, String> colNgaySinh;
    @FXML private TableColumn<SinhVien, String> colGioiTinh;
    @FXML private TableColumn<SinhVien, String> colLop;
    @FXML private TableColumn<SinhVien, String> colTrangThai;

    private ObservableList<SinhVien> list = FXCollections.observableArrayList();

    // FORM
    @FXML private TextField txtMaSV, txtHoDem, txtTen, txtGioiTinh;
    @FXML private DatePicker dpNgaySinh;

    // 🔥 TÊN KHOA
    @FXML private TextField txtTenKhoa;

    @FXML
    public void initialize() {

        colMaSV.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMaSV()));
        colHoTen.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getHoDem() + " " + data.getValue().getTen()));
        colNgaySinh.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getNgaySinh() != null ? data.getValue().getNgaySinh().toString() : ""));
        colGioiTinh.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getGioiTinh()));
        colLop.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMaLop()));
        colTrangThai.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTrangThaiSV()));

        tableSinhVien.setItems(list);

        loadAll();
    }

    // LOAD ALL
    private void loadAll() {
        list.setAll(svDAO.getAllSinhVien());
    }

    // 🔍 TÌM KIẾM
    @FXML
    public void handleTimKiem() {

        String maSV = txtMaSV.getText().trim();
        String tenKhoa = txtTenKhoa.getText().trim();

        // 1. ƯU TIÊN MÃ SV
        if (!maSV.isEmpty()) {
            SinhVien sv = svDAO.getSinhVienByMa(maSV);

            if (sv != null) {
                list.setAll(sv);
                fillForm(sv);
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không tìm thấy!");
            }
        }

        // 2. TÌM THEO KHOA
        else if (!tenKhoa.isEmpty()) {

            List<SinhVien> ds = svDAO.getSinhVienByTenKhoa(tenKhoa);

            list.setAll(ds);

            showAlert(Alert.AlertType.INFORMATION,
                    "Kết quả", "Tìm thấy " + ds.size() + " sinh viên");
        }

        // 3. LOAD ALL
        else {
            loadAll();
        }
    }

    // ➕ THÊM
    @FXML
    public void handleThem() {
        SinhVien sv = new SinhVien();

        sv.setMaSV(txtMaSV.getText());
        sv.setHoDem(txtHoDem.getText());
        sv.setTen(txtTen.getText());
        sv.setNgaySinh(dpNgaySinh.getValue());
        sv.setGioiTinh(txtGioiTinh.getText());

        svDAO.insertSinhVien(sv);

        loadAll();
        clearForm();
    }

    // ✏️ SỬA
    @FXML
    public void handleSua() {
        SinhVien sv = new SinhVien();

        sv.setMaSV(txtMaSV.getText());
        sv.setHoDem(txtHoDem.getText());
        sv.setTen(txtTen.getText());
        sv.setNgaySinh(dpNgaySinh.getValue());
        sv.setGioiTinh(txtGioiTinh.getText());

        svDAO.updateSinhVien(sv);

        loadAll();
    }

    // CLICK TABLE
    @FXML
    public void handleClickTable() {
        SinhVien sv = tableSinhVien.getSelectionModel().getSelectedItem();
        if (sv != null) fillForm(sv);
    }

    // HELPER
    private void fillForm(SinhVien sv) {
        txtMaSV.setText(sv.getMaSV());
        txtHoDem.setText(sv.getHoDem());
        txtTen.setText(sv.getTen());
        dpNgaySinh.setValue(sv.getNgaySinh());
        txtGioiTinh.setText(sv.getGioiTinh());
    }

    private void clearForm() {
        txtMaSV.clear();
        txtHoDem.clear();
        txtTen.clear();
        dpNgaySinh.setValue(null);
        txtGioiTinh.clear();
        txtTenKhoa.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}