package dao;

import dao.nconnguoidao.SinhVienDAO;
import dao.nhethongdao.NguoiDungDAO;
import model.nconnguoi.SinhVien;
import model.nhethong.NguoiDung;

public class testdao {
    public static void main(String[] args){
       /* SinhVien sv = new SinhVien();

        //1. Tạo đối ượng sinh viên mới
        sv.setMaSV("092303");
        sv.setHoDem("Nguyễn Ngoc");
        sv.setTen("khanh");
        sv.setNgaySinh(LocalDate.of(2100, 5, 20));
        sv.setGioiTinh("Nữ");
        sv.setNamNhapHoc(2023);
        sv.setMaLop("LH01"); // Phải tồn tại trong bảng LopHanhChinh
        sv.setSdt("0912345678");
        sv.setEmail("an.nv@gmail.com");
        sv.setTrangThaiSV("Đang học");
        sv.setMaHuyen("001"); // Phải tồn tại trong bảng QuanHuyen */


      /*  // 2. Gọi DAO để lưu vào SQL Server
        SinhVienDAO svdao = new SinhVienDAO();
        System.out.println("\n---------- TIM KIEM SINH VIEN THEO MA ----------");
        String maCanTim = "01243"; // Thay bang mot ma co that trong DB cua ban
        SinhVien svFound = svdao.getSinhVienByMa(maCanTim);

        if (svFound != null) {
            System.out.println("Da tim thay: " + svFound.getHoDem() + " " + svFound.getTen());
            System.out.println("Email: " + svFound.getEmail());
        } else {
            System.out.println("Khong tim thay sinh vien co ma: " + maCanTim);
        }*/

       /*List<SinhVien> ds = svdao.getAllSinhVien();
        if (ds.isEmpty()) {
            System.out.println("Thong bao: Khong co du lieu sinh vien trong Database.");
        } else {
            for (SinhVien sv : ds) {
                System.out.println("Ma SV: " + sv.getMaSV()
                        + " | Ho ten: " + sv.getHoDem() + " " + sv.getTen()
                        + " | Lop: " + sv.getMaLop()
                        + " | Ngay sinh: " + sv.getNgaySinh());
            }
        } */
        //svdao.insertSinhVien(sv);


        //SinhVienDAO svd = new SinhVienDAO();
        //svd.deleteSinhVien("01232");


        /*// test nhan vien
        NhanVien nv = new NhanVien();
        nv.setMaNV("NV999");
        nv.setHoDem("Phạm Minh");
        nv.setTen("Hoàng");
        nv.setNgaySinh(LocalDate.of(1985, 12, 30));
        nv.setGioiTinh("Nữ");
        nv.setLoaiNV("Giảng viên");
        nv.setMaKhoa("CK"); // Phải có mã này trong bảng Khoa
        nv.setHocVan("Tiến sĩ");
        nv.setSdt("0912345678");
        nv.setEmail("hoangpm@university.edu.vn");

            NhanVienDAO nvd = new NhanVienDAO();
            //nvd.insertNhanVien(nv);
            //nvd.updateNhanVien(nv);
            nvd.deleteNhanVien("NV999");
        */

        /*/*test QuanHuyen
        QuanHuyen qh = new QuanHuyen();
        qh.setMaHuyen("1111");
        qh.setMaTinh("01");
        qh.setTenHuyen("Đông Anh");
        dao.QuanHuyenDAO qhd = new dao.QuanHuyenDAO();
        qhd.insertQuanHuyen(qh);
        qhd.deleteQuanHuyen("1111");

         */

       /* NguoiDung nd = new NguoiDung();
        nd.setUsername("admin2");
        nd.setMatKhau("1234");
        nd.setUsertype("AD");
        nd.setMaNV("");
        nd.setMaSV("");
        NguoiDungDAO ndd = new NguoiDungDAO();
        ndd.insertNguoiDung(nd);*/

    }
}
