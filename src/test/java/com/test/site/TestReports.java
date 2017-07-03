package com.test.site;

import com.aventstack.extentreports.Status;
import com.report.factory.ExtentTestManager;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class TestReports extends UserBaseTest {


    @Test(dataProvider = "dataa")
    public void TestReports(String a, String b) throws Exception {

        ExtentTestManager.extentTestNode.get().getModel().setName(a);
        ExtentTestManager.extentTestNode.get().log(Status.INFO, "Hello");

        Assert.assertEquals(3,4);
    }


    @DataProvider(name = "dataa")
    public Object[][] provideData(){

        return new Object[][]{{"Test001","wer"}, {"Test002","sadf"}};
    }
}
