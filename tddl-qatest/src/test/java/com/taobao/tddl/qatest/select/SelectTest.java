package com.taobao.tddl.qatest.select;

/**
 *  Copyright(c) 2010 taobao. All rights reserved.
 *  通用产品测试
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.taobao.tddl.qatest.BaseAndorTestCase;
import com.taobao.tddl.qatest.BaseTestCase;
import com.taobao.tddl.qatest.util.ExecuteTableName;
import com.taobao.tddl.qatest.util.Validator;

/**
 * Comment for LocalServerSelectTest
 * <p/>
 * Author By: yaolingling.pt Created Date: 2012-2-17 上午11:30:55
 */
@RunWith(Parameterized.class)
public class SelectTest extends BaseAndorTestCase {

    @Parameters(name = "{index}:table={0}")
    public static List<String[]> prepare() {
        return Arrays.asList(ExecuteTableName.studentTable(dbType));
    }

    public SelectTest(String studentTableName){
        BaseTestCase.studentTableName = studentTableName;
    }

    @Before
    public void initData() throws Exception {
        Validator.con = Validator.getConnection();
        Validator.andorCon = us.getConnection();
        andorUpdateData("delete from  " + studentTableName, null);
        mysqlUpdateData("delete from  " + studentTableName, null);
    }

    @After
    public void clearData() throws Exception {
        prepareData.psConRcRsClose(rc, rs);
    }

    @Test
    public void selectAllFieldTest() throws Exception {
        andorUpdateData("insert into " + studentTableName + " (id,name,school) values (?,?,?)",
            Arrays.asList(new Object[] { RANDOM_ID, name, school }));
        mysqlUpdateData("insert into " + studentTableName + " (id,name,school) values (?,?,?)",
            Arrays.asList(new Object[] { RANDOM_ID, name, school }));
        String sql = "select * from " + studentTableName + " where id=" + RANDOM_ID;
        String[] columnParam = { "NAME", "SCHOOL" };
        selectOrderAssert(sql, columnParam, Collections.EMPTY_LIST);
    }

    /**
     * 查询的列带为*，并且和其他函数操作一起查询
     */
    @Test
    public void selectAllFieldWithFuncTest() throws Exception {
        andorUpdateData("insert into " + studentTableName + " (id,name,school) values (?,?,?)",
            Arrays.asList(new Object[] { RANDOM_ID, name, school }));
        mysqlUpdateData("insert into " + studentTableName + " (id,name,school) values (?,?,?)",
            Arrays.asList(new Object[] { RANDOM_ID, name, school }));
        String sql = "select *,count(*) from " + studentTableName + " where id=" + RANDOM_ID;
        String[] columnParam = { "NAME", "SCHOOL", "id", "count(*)" };
        selectOrderAssert(sql, columnParam, Collections.EMPTY_LIST);
    }

    @Test
    public void selectSomeFieldTest() throws Exception {
        andorUpdateData("insert into " + studentTableName + " (id,name,school) values (?,?,?)",
            Arrays.asList(new Object[] { RANDOM_ID, name, school }));
        mysqlUpdateData("insert into " + studentTableName + " (id,name,school) values (?,?,?)",
            Arrays.asList(new Object[] { RANDOM_ID, name, school }));
        String sql = "select id,name from " + studentTableName + " where id=" + RANDOM_ID;
        String[] columnParam = { "NAME", "ID" };
        selectOrderAssert(sql, columnParam, Collections.EMPTY_LIST);

        sql = "select id,name from " + studentTableName + " where name= ?";
        List<Object> param = new ArrayList<Object>();
        param.add(name);
        selectOrderAssert(sql, columnParam, param);
    }

    /**
     * 带引号的特殊字符
     * 
     * @throws Exception
     */
    @Test
    public void selectWithQuotationTest() throws Exception {
        String name = "as'sdfd's";
        andorUpdateData("insert into " + studentTableName + " (id,name,school) values (?,?,?)",
            Arrays.asList(new Object[] { RANDOM_ID, name, school }));
        mysqlUpdateData("insert into " + studentTableName + " (id,name,school) values (?,?,?)",
            Arrays.asList(new Object[] { RANDOM_ID, name, school }));
        String[] columnParam = { "NAME", "ID" };

        String sql = "select id,name from " + studentTableName + " where name= ?";
        List<Object> param = new ArrayList<Object>();
        param.add(name);
        selectOrderAssert(sql, columnParam, param);

        sql = "select id,name from " + studentTableName + " where name= 'as\\'sdfd\\'s'";
        selectOrderAssert(sql, columnParam, null);
    }

    @Test
    public void selectWithNotExistDateTest() throws Exception {
        andorUpdateData("insert into " + studentTableName + " (id,name,school) values (?,?,?)",
            Arrays.asList(new Object[] { RANDOM_ID, name, school }));
        mysqlUpdateData("insert into " + studentTableName + " (id,name,school) values (?,?,?)",
            Arrays.asList(new Object[] { RANDOM_ID, name, school }));
        String sql = "select * from " + studentTableName + " where id=" + RANDOM_ID + 1;

        selectConutAssert(sql, Collections.EMPTY_LIST);
    }

    @Test
    public void selectWithNotExistFileTest() throws Exception {
        andorUpdateData("insert into " + studentTableName + " (id,name,school) values (?,?,?)",
            Arrays.asList(new Object[] { RANDOM_ID, name, school }));
        String sql = "select * from " + studentTableName + " where pk=" + RANDOM_ID;
        try {
            rc = andorQueryData(sql, null);
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("column: PK is not existed in"));
        }
    }

    @Test
    public void selectWithNotExistTableTest() throws Exception {
        String sql = "select * from stu where pk=" + RANDOM_ID;
        try {
            rc = andorQueryData(sql, null);
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getCause().getCause().getMessage().contains("STU is not found"));
        }
    }
}
