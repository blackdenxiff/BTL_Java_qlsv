package model.ndialy;

public class QuanHuyen {
    private String maHuyen;
    private String tenHuyen;
    private String maTinh;

    public QuanHuyen(){

    }
    public QuanHuyen(String maHuyen, String tenHuyen, String maTinh) {
        this.maHuyen = maHuyen;
        this.tenHuyen = tenHuyen;
        this.maTinh = maTinh;
    }

    public String getMaHuyen() {
        return maHuyen;
    }

    public void setMaHuyen(String maHuyen) {
        this.maHuyen = maHuyen;
    }

    public String getTenHuyen() {
        return tenHuyen;
    }

    public void setTenHuyen(String tenHuyen) {
        this.tenHuyen = tenHuyen;
    }

    public String getMaTinh() {
        return maTinh;
    }

    public void setMaTinh(String maTinh) {
        this.maTinh = maTinh;
    }
}
