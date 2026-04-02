package controller.nhanvien;

import dao.quanlydao.DangKiDAO;
import dao.nconnguoidao.SinhVienDAO;
import model.nconnguoi.SinhVien;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class XuatTaiLieuController {

    private DangKiDAO dkDAO = new DangKiDAO();
    private SinhVienDAO svDAO = new SinhVienDAO();

    public void inGiayXacNhanBangDiem(String maSV) {
        SinhVien sv = svDAO.getSinhVienByMa(maSV);
        if (sv == null) {
            System.out.println("Không tìm thấy sinh viên!");
            return;
        }

        List<Map<String, Object>> bangDiem = dkDAO.getChiTietBangDiem(maSV);
        String tenFile = "BangDiem_" + maSV + ".txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tenFile))) {
            // 1. Vẽ Header (Quốc hiệu)
            bw.write("                  CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM"); bw.newLine();
            bw.write("                        Độc lập - Tự do - Hạnh phúc"); bw.newLine();
            bw.write("                        ---------------------------"); bw.newLine();
            bw.newLine();
            bw.write("                            GIẤY XÁC NHẬN BẢNG ĐIỂM"); bw.newLine();
            bw.newLine();

            // 2. Thông tin sinh viên
            bw.write("Họ và tên: " + sv.getHoDem() + " " + sv.getTen() + "        Mã SV: " + sv.getMaSV()); bw.newLine();
            bw.write("Lớp hành chính: " + sv.getMaLop()); bw.newLine();
            bw.write("================================================================================="); bw.newLine();

            // 3. Header của Bảng điểm
            bw.write(String.format("| %-10s | %-30s | %-7s | %-10s | %-7s |",
                    "Mã Môn", "Tên Môn Học", "Tín chỉ", "Học Kỳ", "Điểm")); bw.newLine();
            bw.write("================================================================================="); bw.newLine();

            // 4. Duyệt dữ liệu và thuật toán tính GPA
            double tongDiemNhanTinChi = 0;
            int tongTinChiTichLuy = 0;

            for (Map<String, Object> row : bangDiem) {
                String maMH = (String) row.get("maMH");
                String tenMH = (String) row.get("tenMH");
                int stc = (int) row.get("soTinChi");
                int hocKy = (int) row.get("hocKy");
                double diem = (double) row.get("diemSo");

                bw.write(String.format("| %-10s | %-30s | %-7d | HK%d-%d   | %-7.2f |",
                        maMH, tenMH, stc, hocKy, (int)row.get("namHoc"), diem)); bw.newLine();

                // Chỉ tính tín chỉ tích lũy nếu điểm >= 4.0 (Qua môn)
                if (diem >= 4.0) {
                    tongTinChiTichLuy += stc;
                    tongDiemNhanTinChi += (diem * stc);
                }
            }
            bw.write("================================================================================="); bw.newLine();

            // 5. Tính và in Tổng kết
            double gpa = (tongTinChiTichLuy > 0) ? (tongDiemNhanTinChi / tongTinChiTichLuy) : 0.0;

            bw.newLine();
            bw.write(">> TỔNG TÍN CHỈ TÍCH LŨY: " + tongTinChiTichLuy + " tín chỉ"); bw.newLine();
            bw.write(String.format(">> ĐIỂM TRUNG BÌNH CHUNG TÍCH LŨY (GPA): %.2f / 10.0", gpa)); bw.newLine();

            // Xếp loại học lực
            String xepLoai = "Kém";
            if (gpa >= 8.5) xepLoai = "Giỏi";
            else if (gpa >= 7.0) xepLoai = "Khá";
            else if (gpa >= 5.5) xepLoai = "Trung bình";
            else if (gpa >= 4.0) xepLoai = "Yếu";

            bw.write(">> XẾP LOẠI HỌC LỰC: " + xepLoai); bw.newLine();
            bw.newLine();
            bw.write("                                            Ngày " + LocalDate.now().getDayOfMonth() + " tháng " + LocalDate.now().getMonthValue() + " năm " + LocalDate.now().getYear()); bw.newLine();
            bw.write("                                            XÁC NHẬN CỦA PHÒNG ĐÀO TẠO"); bw.newLine();

            System.out.println("Đã xuất Bảng điểm thành công ra file: " + tenFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}