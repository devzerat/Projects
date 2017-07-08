package com.dev.zerat.goplabresult;

/**
 * Created by HP on 8.07.2017.
 */

public class ParamsInfo {
    private String m_tckn;
    private String m_fileNo;
    private String m_resultDescription;
    private int m_resultCode;

    public int getresultCode() {
        return m_resultCode;
    }

    public void setresultCode(int m_resultCode) {
        this.m_resultCode = m_resultCode;
    }

    public String getresultDescription() {
        return m_resultDescription;
    }

    public void setresultDescription(String m_resultDescription) {
        this.m_resultDescription = m_resultDescription;
    }


    public String gettckn() {
        return m_tckn;
    }

    public void settckn(String m_tckn) {
        this.m_tckn = m_tckn;
    }

    public String getfileNo() {
        return m_fileNo;
    }

    public void setfileNo(String m_mfileNo) {
        this.m_fileNo = m_mfileNo;
    }

    public String geturl() {
        return m_url;
    }

    public void seturl(String m_url) {
        this.m_url = m_url;
    }

    private String m_url;

    public ParamsInfo(String tckn, String fileno, String url) {
        m_tckn = tckn;
        m_fileNo = fileno;
        m_url = url;
    }
}
