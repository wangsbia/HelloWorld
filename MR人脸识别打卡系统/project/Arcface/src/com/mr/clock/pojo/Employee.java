package com.mr.clock.pojo;

public class Employee {
    private Integer id;
    private String name;
    private String code;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Employee(Integer id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public int hashCode(){
        final int prime=31;
        int result=1;
        result=prime*result+((id==null)? 0:id.hashCode());
        return result;
    }

    public boolean equals(Object obj){
        if(this==obj){
            return true;
        }
        if(obj==null){
            return false;
        }
        if(getClass()!=obj.getClass()){
            return false;
        }
        Employee other=(Employee) obj;
        if(id==null){
            if(other.id!=null){
                return false;
            }
        }else if(!id.equals(other.id)){
            return false;
        }
        return true;
    }

}
