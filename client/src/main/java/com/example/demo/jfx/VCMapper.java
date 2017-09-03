package com.example.demo.jfx;

import java.nio.file.Path;

/**
 * .fxmlファイルとコントローラのマッピング用クラス
 * @author tsuchiya
 */
public interface VCMapper {

	/**
	 * 指定した.fxmlファイルにマッピングされたコントローラを返却する。
	 * @author tsuchiya
	 */
	Class<?> getControllerClass(Path path);
}
