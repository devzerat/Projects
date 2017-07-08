package com.dev.zerat.goplabresult.entity;

import java.io.Serializable;

/**
 * Created by HP on 2.07.2017.
 */

public class LabResultInfo implements Serializable {
    private  String m_Date;
    private  String m_Department;
    private  int m_id;

    public String getDate() {
        return m_Date;
    }

    public void setDate(String m_Date) {
        this.m_Date = m_Date;
    }

    public String getDepartment() {
        return m_Department;
    }

    public void setDepartment(String m_Department) {
        this.m_Department = m_Department;
    }

    public int getId() {
        return m_id;
    }

    public void setId(int m_id) {
        this.m_id = m_id;
    }

    public String getUrl() {
        return m_Url;
    }

    public void setUrl(String m_Url) {
        this.m_Url = m_Url;
    }

    private  String m_Url;

    public  LabResultInfo(int id,String date,String department,String url)
    {
        m_id=id;
        m_Date=date;
        m_Department=department;
        m_Url=url;
    }
}
