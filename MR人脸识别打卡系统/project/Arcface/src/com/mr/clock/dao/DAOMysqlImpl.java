package com.mr.clock.dao;

import com.mr.clock.pojo.Employee;
import com.mr.clock.pojo.User;
import com.mr.clock.pojo.WorkTime;
import com.mr.clock.util.JDBCUtil;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.Callable;

public  class DAOMysqlImpl implements DAO {
    private Connection con;
    private PreparedStatement ps;
    private ResultSet re;
    private Statement stmt;

    @Override
    public Set<Employee> getAllEmp() {
        return null;
    }

    public Employee getEmp(int id) {
        String sql = "select name ,code from t_emp where id=?";
        con = JDBCUtil.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            re = ps.executeQuery();
            if (re.next()) {
                String name = re.getString("name");
                String code = re.getString("code");
                Employee e = new Employee(id, name, code);
                return e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtil.close(stmt,ps, re);
        }
        return null;
    }

    @Override
    public Employee getEmp(String code) {
        return null;
    }

    public void addEmp(Employee e) {
        String sql = "insert into t_emp(name,code) values (?,?)";
        con = JDBCUtil.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, e.getName());
            ps.setString(2, e.getCode());
            ps.executeUpdate();
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            JDBCUtil.close(stmt,ps, re);
        }
    }

    public void deleteEmp(Integer id) {
        String sql = "delete from t_emp where id=?";
        con = JDBCUtil.getConnection();
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e2) {
            e2.printStackTrace();
        } finally {
            JDBCUtil.close(stmt,ps, re);
        }
    }

    @Override
    public WorkTime getWork() {
        return null;
    }

    public void updateWorkTime(WorkTime time){
        String sql="update t_work_time set start =?,end=? ";
        con=JDBCUtil.getConnection();
        try {
            ps=con.prepareStatement(sql);
            ps.setString(1,time.getStart());
            ps.setString(2,time.getEnd());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JDBCUtil.close(stmt,ps,re);
        }
    }

    @Override
    public void addCLockInRecord(int empID, Date now) {

    }

    @Override
    public void deleteClockInRecord(int empID) {

    }

    public String [][] getAllClockInRecord(){
        HashSet<String[]> set=new HashSet<>();
        String sql="select emp_id,lock_in_time from t_lock_in_record";
        con=JDBCUtil.getConnection();
        try {
            stmt=con.createStatement();
            re=stmt.executeQuery(sql);
            while (re.next()){
                String emp_id=re.getString("emp_id");
                String lock_in_time=re.getString("lock_in_time");
                set.add(new String[]{emp_id,lock_in_time});
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            JDBCUtil.close(stmt,ps,re);
        }if (set.isEmpty()){
            return null;
        }else {
            String result[][]=new String[set.size()][2];
            Iterator<String[]> it=set.iterator();
            for (int i = 0; it.hasNext(); i++) {
                result[i]=it.next();
            }
            return result;
        }
    }

    @Override
    public boolean userLogin(User user) {
        return false;
    }

    public class DAOFactory{
        public static DAO getDAO(){
            return new DAOMysqlImpl();
        }
    }
}
