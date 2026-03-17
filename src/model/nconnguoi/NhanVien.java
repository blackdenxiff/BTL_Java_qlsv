package model.nconnguoi;

import java.time.LocalDate;

public class NhanVien {
    private String maNV;
    private String hoDem;
    private String ten;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String loaiNV;
    private String maKhoa; // Khóa ngoại
    private String hocVan;
    private String sdt;
    private String email;

    public NhanVien() {

    }

    public NhanVien(String maNV, String hoDem, String ten, LocalDate ngaySinh, String gioiTinh, String loaiNV, String maKhoa, String hocVan, String sdt, String email) {
        this.maNV = maNV;
        this.hoDem = hoDem;
        this.ten = ten;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.loaiNV = loaiNV;
        this.maKhoa = maKhoa;
        this.hocVan = hocVan;
        this.sdt = sdt;
        this.email = email;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
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

    public String getLoaiNV() {
        return loaiNV;
    }

    public void setLoaiNV(String loaiNV) {
        this.loaiNV = loaiNV;
    }

    public String getMaKhoa() {
        return maKhoa;
    }

    public void setMaKhoa(String maKhoa) {
        this.maKhoa = maKhoa;
    }

    public String getHocVan() {
        return hocVan;
    }

    public void setHocVan(String hocVan) {
        this.hocVan = hocVan;
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
}

