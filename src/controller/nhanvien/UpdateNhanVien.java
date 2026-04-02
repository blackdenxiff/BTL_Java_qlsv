package controller.nhanvien;

import dao.nconnguoidao.NhanVienDAO;
import model.nconnguoi.NhanVien;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UpdateNhanVien {

    private NhanVienDAO nvDAO = new NhanVienDAO();

    // ===== FORM =====
    @FXML private TextField txtMaNV, txtHoDem, txtTen, txtGioiTinh, txtLoaiNV, txtMaKhoa, txtHocVan, txtSDT, txtEmail;
    @FXML private DatePicker dpNgaySinh;

    // ===== TABLE =====
    @FXML private TableView<NhanVien> tableNhanVien;
    @FXML private TableColumn<NhanVien, String> colMaNV, colHoTen, colNgaySinh, colGioiTinh, colLoaiNV, colKhoa;

    private ObservableList<NhanVien> list = FXCollections.observableArrayList();

    // ===== INIT =====
    @FXML
    public void initialize() {

        colMaNV.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getMaNV()));
        colHoTen.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getHoDem() + " " + d.getValue().getTen()));
        colNgaySinh.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                d.getValue().getNgaySinh() != null ? d.getValue().getNgaySinh().toString() : ""));
        colGioiTinh.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getGioiTinh()));
        colLoaiNV.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getLoaiNV()));
        colKhoa.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getMaKhoa()));

        tableNhanVien.setItems(list);

        list.setAll(nvDAO.getAllNhanVien()); // load data
    }

    // ===== CLICK TABLE =====
    @FXML
    public void handleClickTable() {
        NhanVien nv = tableNhanVien.getSelectionModel().getSelectedItem();
        if (nv != null) fillForm(nv);
    }

    // ===== TÌM =====
    @FXML
    public void handleTimKiem() {
        String ma = txtMaNV.getText().trim();
        if (ma.isEmpty()) return;

        NhanVien nv = nvDAO.getNhanVienByMa(ma);

        if (nv != null) {
            list.setAll(nv);
            fillForm(nv);
        } else {
            show("Không tìm thấy!");
        }
    }

    // ===== THÊM =====
    @FXML
    public void handleThem() {
        NhanVien nv = getForm();
        nvDAO.insertNhanVien(nv);
        list.add(nv);
        show("Đã thêm!");
        clear();
    }

    // ===== SỬA =====
    @FXML
    public void handleSua() {
        NhanVien nv = getForm();
        nvDAO.updateNhanVien(nv);

        int i = tableNhanVien.getSelectionModel().getSelectedIndex();
        if (i >= 0) list.set(i, nv);

        show("Đã cập nhật!");
    }

    // ===== HELPER =====
    private NhanVien getForm() {
        NhanVien nv = new NhanVien();
        nv.setMaNV(txtMaNV.getText());
        nv.setHoDem(txtHoDem.getText());
        nv.setTen(txtTen.getText());
        nv.setNgaySinh(dpNgaySinh.getValue());
        nv.setGioiTinh(txtGioiTinh.getText());
        nv.setLoaiNV(txtLoaiNV.getText());
        nv.setMaKhoa(txtMaKhoa.getText());
        nv.setHocVan(txtHocVan.getText());
        nv.setSdt(txtSDT.getText());
        nv.setEmail(txtEmail.getText());
        return nv;
    }

    private void fillForm(NhanVien nv) {
        txtMaNV.setText(nv.getMaNV());
        txtHoDem.setText(nv.getHoDem());
        txtTen.setText(nv.getTen());
        dpNgaySinh.setValue(nv.getNgaySinh());
        txtGioiTinh.setText(nv.getGioiTinh());
        txtLoaiNV.setText(nv.getLoaiNV());
        txtMaKhoa.setText(nv.getMaKhoa());
        txtHocVan.setText(nv.getHocVan());
        txtSDT.setText(nv.getSdt());
        txtEmail.setText(nv.getEmail());
    }

    private void clear() {
        txtMaNV.clear(); txtHoDem.clear(); txtTen.clear();
        dpNgaySinh.setValue(null);
        txtGioiTinh.clear(); txtLoaiNV.clear(); txtMaKhoa.clear();
        txtHocVan.clear(); txtSDT.clear(); txtEmail.clear();
    }

    private void show(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.show();
    }
}