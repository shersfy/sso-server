package org.young.sso.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.young.sso.sdk.utils.HttpUtil;

@RestController
@RequestMapping("/fs")
public class FileSystemController extends BaseController {
	
	@GetMapping("/file/fullpath")
	public String getAccessPath(String path) {
		return HttpUtil.concatUrl(properties.getDfsServer(), path);
	}

}
