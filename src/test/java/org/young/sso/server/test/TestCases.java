package org.young.sso.server.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.young.sso.sdk.resource.LoginType;
import org.young.sso.server.beans.IdInfo;
import org.young.sso.server.controller.BaseController;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class TestCases {
	
	protected static final Logger LOGGER   = LoggerFactory.getLogger(BaseController.class);

	@Test
	public void test01() throws Exception {
		IdInfo id = new IdInfo();
		id.setUsername("admin");
		id.setK("dsdsds");
		id.setType(LoginType.qrcode);
		
		System.out.println(id.toString());
	}

	@Test
	public void test02() throws Exception {
		
	}
		
}
