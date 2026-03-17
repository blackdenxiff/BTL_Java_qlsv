package model.quanly;

public class LopHocPhan {
    private String maLHP;
    private int namHoc;
    private int hocKy;
    private int gioiHanSV;
    private String maMH;   // Khóa ngoại
    private String maNV;   // Khóa ngoại (Giảng viên)
    private String caHoc;
    private String phongHoc;

    public LopHocPhan(){

    }
    public LopHocPhan(String maLHP, int namHoc, int hocKy, int gioiHanSV, String maMH, String maNV, String caHoc, String phongHoc) {
        this.maLHP = maLHP;
        this.namHoc = namHoc;
        this.hocKy = hocKy;
        this.gioiHanSV = gioiHanSV;
        this.maMH = maMH;
        this.maNV = maNV;
        this.caHoc = caHoc;
        this.phongHoc = phongHoc;
    }

    public String getMaLHP() {
        return maLHP;
    }

    public void setMaLHP(String maLHP) {
        this.maLHP = maLHP;
    }

    public int getNamHoc() {
        return namHoc;
    }

    public void setNamHoc(int namHoc) {
        this.namHoc = namHoc;
    }

    public int getHocKy() {
        return hocKy;
    }

    public void setHocKy(int hocKy) {
        this.hocKy = hocKy;
    }

    public int getGioiHanSV() {
        return gioiHanSV;
    }

    public void setGioiHanSV(int gioiHanSV) {
        this.gioiHanSV = gioiHanSV;
    }

    public String getMaMH() {
        return maMH;
    }

    public void setMaMH(String maMH) {
        this.maMH = maMH;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getCaHoc() {
        return caHoc;
    }

    public void setCaHoc(String caHoc) {
        this.caHoc = caHoc;
    }

    public String getPhongHoc() {
        return phongHoc;
    }

    public void setPhongHoc(String phongHoc) {
        this.phongHoc = phongHoc;
    }
}
