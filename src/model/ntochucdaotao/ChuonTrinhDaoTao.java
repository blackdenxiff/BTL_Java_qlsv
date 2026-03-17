package model.ntochucdaotao;

public class ChuonTrinhDaoTao {
    private String maCTDT;
    private String tenCTDT;
    private int tongTinChi;
    private int namApDung;
    private String maKhoa;

    public ChuonTrinhDaoTao(){

    }
    public ChuonTrinhDaoTao(String maCTDT, String tenCTDT, int tongTinChi, int namApDung, String maKhoa) {
        this.maCTDT = maCTDT;
        this.tenCTDT = tenCTDT;
        this.tongTinChi = tongTinChi;
        this.namApDung = namApDung;
        this.maKhoa = maKhoa;
    }

    public String getMaCTDT() {
        return maCTDT;
    }

    public void setMaCTDT(String maCTDT) {
        this.maCTDT = maCTDT;
    }

    public String getTenCTDT() {
        return tenCTDT;
    }

    public void setTenCTDT(String tenCTDT) {
        this.tenCTDT = tenCTDT;
    }

    public int getTongTinChi() {
        return tongTinChi;
    }

    public void setTongTinChi(int tongTinChi) {
        this.tongTinChi = tongTinChi;
    }

    public int getNamApDung() {
        return namApDung;
    }

    public void setNamApDung(int namApDung) {
        this.namApDung = namApDung;
    }

    public String getMaKhoa() {
        return maKhoa;
    }

    public void setMaKhoa(String maKhoa) {
        this.maKhoa = maKhoa;
    }

}
