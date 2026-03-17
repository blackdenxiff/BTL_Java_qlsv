package dao;

import model.nconnguoi.NhanVien;
import model.nconnguoi.SinhVien;
import dao.SinhVienDAO;
import model.ndialy.QuanHuyen;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class testdao {
    public static void main(String[] args){
        SinhVien sv = new SinhVien();

        //1. Tạo đối ượng sinh viên mới
        sv.setMaSV("092383");
        sv.setHoDem("Nguyễn Ngoc");
        sv.setTen("khanh");
        sv.setNgaySinh(LocalDate.of(2100, 5, 20));
        sv.setGioiTinh("Nữ");
        sv.setNamNhapHoc(2023);
        sv.setMaLop("LH01"); // Phải tồn tại trong bảng LopHanhChinh
        sv.setSdt("0912345678");
        sv.setEmail("an.nv@gmail.com");
        sv.setTrangThaiSV("Đang học");
        sv.setMaHuyen("001"); // Phải tồn tại trong bảng QuanHuyen

        // 2. Gọi DAO để lưu vào SQL Server
        SinhVienDAO dao = new SinhVienDAO();
        dao.insertSinhVien(sv);


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

    }
}
