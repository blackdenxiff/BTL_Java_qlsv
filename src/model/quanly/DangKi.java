package model.quanly;

public class DangKi {
    private String maSV;   // Khóa chính/ngoại
    private String maLHP;  // Khóa chính/ngoại
    private double diemSo;
    private String trangThai;

    public DangKi(String maSV, String maLHP, double diemSo, String trangThai) {
        this.maSV = maSV;
        this.maLHP = maLHP;
        this.diemSo = diemSo;
        this.trangThai = trangThai;
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public String getMaLHP() {
        return maLHP;
    }

    public void setMaLHP(String maLHP) {
        this.maLHP = maLHP;
    }

    public double getDiemSo() {
        return diemSo;
    }

    public void setDiemSo(double diemSo) {
        this.diemSo = diemSo;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }
}
