package controller.nhanvien;

import dao.quanlydao.DangKiDAO;
import dao.quanlydao.LopHocPhanDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import model.nconnguoi.SinhVien;
import model.quanly.LopHocPhan;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class LopHocPhanController {

    private LopHocPhanDAO lhpDAO = new LopHocPhanDAO();
    private DangKiDAO dangKiDAO = new DangKiDAO();

    // ===== FORM =====
    @FXML private TextField txtMaLHP;
    @FXML private TextField txtMaGV;
    @FXML private TextField txtCaHoc;
    @FXML private TextField txtPhong;
    @FXML private TextField txtHocKy;
    @FXML private TextField txtNamHoc;

    @FXML private TextField txtMaLHPExport;
    @FXML private TextField txtFile;

    // ================= PHÂN CÔNG =================
    @FXML
    private void handlePhanCong() {
        try {
            if (txtMaLHP.getText().isEmpty() || txtMaGV.getText().isEmpty()) {
                show("Lỗi", "Nhập thiếu dữ liệu!");
                return;
            }

            LopHocPhan lhp = new LopHocPhan();
            lhp.setMaLHP(txtMaLHP.getText().trim());
            lhp.setMaNV(txtMaGV.getText().trim());
            lhp.setCaHoc(txtCaHoc.getText().trim());
            lhp.setPhongHoc(txtPhong.getText().trim());
            lhp.setHocKy(Integer.parseInt(txtHocKy.getText().trim()));
            lhp.setNamHoc(Integer.parseInt(txtNamHoc.getText().trim()));

            // gọi luôn trong class (KHÔNG tạo controller mới)
            boolean ok = phanCongGiangDay(lhp);

            if (ok) {
                show("OK", "Phân công thành công!");
                clearForm();
            } else {
                show("Lỗi", "Trùng phòng hoặc giảng viên!");
            }

        } catch (Exception e) {
            show("Lỗi", "Sai dữ liệu!");
        }
    }

    // ================= XUẤT FILE =================
    @FXML
    private void handleXuat() {
        try {
            String maLHP = txtMaLHPExport.getText().trim();
            String file = txtFile.getText().trim();

            if (maLHP.isEmpty()) {
                show("Lỗi", "Nhập mã LHP!");
                return;
            }

            if (file.isEmpty()) file = "diemdanh.txt";

            xuatDanhSachDiemDanh(maLHP, file);

            show("OK", "Xuất file thành công!");

        } catch (Exception e) {
            show("Lỗi", "Xuất file thất bại!");
        }
    }

    // ================= LOGIC =================
    private boolean phanCongGiangDay(LopHocPhan lhp) {

        if (lhpDAO.checkTrungPhong(
                lhp.getCaHoc(),
                lhp.getPhongHoc(),
                lhp.getHocKy(),
                lhp.getNamHoc())) {

            return false;
        }

        if (lhpDAO.checkGiangVienBan(
                lhp.getMaNV(),
                lhp.getCaHoc(),
                lhp.getHocKy(),
                lhp.getNamHoc())) {

            return false;
        }

        lhpDAO.insertLHP(lhp);
        return true;
    }

    private void xuatDanhSachDiemDanh(String maLHP, String file) {

        List<SinhVien> list = dangKiDAO.getSinhVienByLHP(maLHP);

        if (list.isEmpty()) {
            show("Cảnh báo", "Không có sinh viên!");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

            bw.write("===== DANH SÁCH LỚP " + maLHP + " =====");
            bw.newLine();

            int i = 1;
            for (SinhVien sv : list) {
                bw.write(i++ + ". " + sv.getMaSV() + " - "
                        + sv.getHoDem() + " " + sv.getTen());
                bw.newLine();
            }

        } catch (Exception e) {
            show("Lỗi", "Không ghi được file!");
        }
    }

    // ================= HỖ TRỢ =================
    private void clearForm() {
        txtMaLHP.clear();
        txtMaGV.clear();
        txtCaHoc.clear();
        txtPhong.clear();
        txtHocKy.clear();
        txtNamHoc.clear();
    }

    private void show(String t, String c) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(c);
        a.showAndWait();
    }
}