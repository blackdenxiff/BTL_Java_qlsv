package controller.nhanvien;

import dao.quanlydao.DangKiDAO;
import dao.quanlydao.LopHocPhanDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import model.nconnguoi.SinhVien;
import model.quanly.LopHocPhan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LopHocPhanController {

    private LopHocPhanDAO lhpDAO = new LopHocPhanDAO();
    private DangKiDAO dangKiDAO = new DangKiDAO();

    // ===== CÁC TRƯỜNG NHẬP LIỆU (FX:ID) =====
    @FXML private TextField txtMaLHP;
    @FXML private TextField txtMaGV;
    @FXML private TextField txtCaHoc;
    @FXML private TextField txtPhong;
    @FXML private TextField txtHocKy;
    @FXML private TextField txtNamHoc;

    @FXML private TextField txtMaLHPExport; // Ô nhập mã LHP để xuất file
    @FXML private TextField txtFile;         // Ô nhập tên file (không còn bắt buộc vì có FileChooser)

    // ================= CHỨC NĂNG PHÂN CÔNG GIẢNG DẠY =================
    @FXML
    private void handlePhanCong() {
        try {
            if (txtMaLHP.getText().isEmpty() || txtMaGV.getText().isEmpty()) {
                show("Lỗi", "Vui lòng nhập đầy đủ Mã LHP và Mã GV!");
                return;
            }

            LopHocPhan lhp = new LopHocPhan();
            lhp.setMaLHP(txtMaLHP.getText().trim());
            lhp.setMaNV(txtMaGV.getText().trim());
            lhp.setCaHoc(txtCaHoc.getText().trim());
            lhp.setPhongHoc(txtPhong.getText().trim());
            lhp.setHocKy(Integer.parseInt(txtHocKy.getText().trim()));
            lhp.setNamHoc(Integer.parseInt(txtNamHoc.getText().trim()));

            if (phanCongGiangDay(lhp)) {
                show("Thành công", "Phân công giảng dạy thành công!");
                clearForm();
            } else {
                show("Lỗi", "Trùng lịch phòng học hoặc giảng viên đã bận ca này!");
            }
        } catch (Exception e) {
            show("Lỗi", "Dữ liệu nhập vào không hợp lệ (Học kỳ/Năm học phải là số)!");
        }
    }

    // ================= CHỨC NĂNG XUẤT FILE EXCEL (FIX LỖI CỦA BẠN) =================
    @FXML
    private void handleXuat() {
        try {
            String maLHP = txtMaLHPExport.getText().trim();

            if (maLHP.isEmpty()) {
                show("Lỗi", "Vui lòng nhập Mã lớp học phần cần xuất!");
                return;
            }

            // 1. Mở cửa sổ chọn vị trí lưu file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Lưu danh sách điểm danh");
            fileChooser.setInitialFileName("DiemDanh_" + maLHP + ".csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel CSV (*.csv)", "*.csv"));

            File savedFile = fileChooser.showSaveDialog(txtMaLHPExport.getScene().getWindow());

            // 2. Tiến hành xuất nếu người dùng chọn file
            if (savedFile != null) {
                xuatDanhSachDiemDanh(maLHP, savedFile.getAbsolutePath());
                show("Thành công", "Đã xuất dữ liệu ra file:\n" + savedFile.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
            show("Lỗi", "Lỗi hệ thống khi xuất file!");
        }
    }

    // ================= LOGIC XỬ LÝ DỮ LIỆU =================
    private boolean phanCongGiangDay(LopHocPhan lhp) {
        if (lhpDAO.checkTrungPhong(lhp.getCaHoc(), lhp.getPhongHoc(), lhp.getHocKy(), lhp.getNamHoc())) return false;
        if (lhpDAO.checkGiangVienBan(lhp.getMaNV(), lhp.getCaHoc(), lhp.getHocKy(), lhp.getNamHoc())) return false;

        lhpDAO.insertLHP(lhp);
        return true;
    }

    private void xuatDanhSachDiemDanh(String maLHP, String filePath) {
        List<SinhVien> list = dangKiDAO.getSinhVienByLHP(maLHP);
        if (list.isEmpty()) {
            show("Cảnh báo", "Lớp này chưa có sinh viên đăng ký!");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            bw.write('\ufeff'); // Ghi mã BOM để Excel đọc được Tiếng Việt
            bw.write("STT;Mã Sinh Viên;Họ Đệm;Tên;Lớp Hành Chính;Ký tên");
            bw.newLine();

            int i = 1;
            for (SinhVien sv : list) {
                bw.write(String.format("%d;%s;%s;%s;%s;", i++, sv.getMaSV(), sv.getHoDem(), sv.getTen(), sv.getMaLop()));
                bw.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        txtMaLHP.clear(); txtMaGV.clear(); txtCaHoc.clear();
        txtPhong.clear(); txtHocKy.clear(); txtNamHoc.clear();
    }

    private void show(String title, String content) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(content);
        a.showAndWait();
    }
}