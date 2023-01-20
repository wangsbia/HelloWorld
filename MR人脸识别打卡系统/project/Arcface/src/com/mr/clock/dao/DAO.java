package com.mr.clock.dao;

import com.mr.clock.pojo.Employee;
import com.mr.clock.pojo.User;
import com.mr.clock.pojo.WorkTime;

import java.util.Date;
import java.util.Set;

public interface DAO{
    public Set<Employee> getAllEmp();

    public Employee getEmp(int id);

    public Employee getEmp(String code);

    public void addEmp(Employee e);

    public void deleteEmp(Integer id);

    public WorkTime getWork();

    public void updateWorkTime(WorkTime time);

    public void addCLockInRecord(int empID, Date now);

    public void deleteClockInRecord(int empID);

    public String [][] getAllClockInRecord();

    public boolean userLogin(User user);
}
