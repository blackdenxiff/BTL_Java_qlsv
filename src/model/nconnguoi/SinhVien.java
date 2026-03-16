package model.nconnguoi;

import java.time.LocalDate;

public class SinhVien {
    private String maSV;
    private String hoDem;
    private String ten;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private int namNhapHoc;
    private String maLop;
    private String sdt;
    private String email;
    private String trangThaiSV;
    private String maHuyen;

    public SinhVien() {
    }
    public SinhVien(String maSV, String hoDem, String ten, LocalDate ngaySinh, String gioiTinh, int namNhapHoc, String maLop, String sdt, String email, String trangThaiSV, String maHuyen) {
        this.maSV = maSV;
        this.hoDem = hoDem;
        this.ten = ten;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.namNhapHoc = namNhapHoc;
        this.maLop = maLop;
        this.sdt = sdt;
        this.email = email;
        this.trangThaiSV = trangThaiSV;
        this.maHuyen = maHuyen;
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public String getHoDem() {
        return hoDem;
    }

    public void setHoDem(String hoDem) {
        this.hoDem = hoDem;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public int getNamNhapHoc() {
        return namNhapHoc;
    }

    public void setNamNhapHoc(int namNhapHoc) {
        this.namNhapHoc = namNhapHoc;
    }

    public String getMaLop() {
        return maLop;
    }

    public void setMaLop(String maLop) {
        this.maLop = maLop;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTrangThaiSV() {
        return trangThaiSV;
    }

    public void setTrangThaiSV(String trangThaiSV) {
        this.trangThaiSV = trangThaiSV;
    }

    public String getMaHuyen() {
        return maHuyen;
    }

    public void setMaHuyen(String maHuyen) {
        this.maHuyen = maHuyen;
    }
}