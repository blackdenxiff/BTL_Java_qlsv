package model.ntochucdaotao;

public class MonHoc {
    private String maMH;
    private String tenMH;
    private int soTC;
    private String loaiMon;
    private String maCTDT;

    public MonHoc(String maMH, String tenMH, int soTC, String loaiMon, String maCTDT) {
        this.maMH = maMH;
        this.tenMH = tenMH;
        this.soTC = soTC;
        this.loaiMon = loaiMon;
        this.maCTDT = maCTDT;
    }

    public String getMaMH() {
        return maMH;
    }

    public void setMaMH(String maMH) {
        this.maMH = maMH;
    }

    public String getTenMH() {
        return tenMH;
    }

    public void setTenMH(String tenMH) {
        this.tenMH = tenMH;
    }

    public int getSoTC() {
        return soTC;
    }

    public void setSoTC(int soTC) {
        this.soTC = soTC;
    }

    public String getLoaiMon() {
        return loaiMon;
    }

    public void setLoaiMon(String loaiMon) {
        this.loaiMon = loaiMon;
    }

    public String getMaCTDT() {
        return maCTDT;
    }

    public void setMaCTDT(String maCTDT) {
        this.maCTDT = maCTDT;
    }
}
