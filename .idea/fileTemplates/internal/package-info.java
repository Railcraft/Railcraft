#parse("File Header.java")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;