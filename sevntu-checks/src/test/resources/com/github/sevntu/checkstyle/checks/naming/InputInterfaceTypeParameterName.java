package com.github.sevntu.checkstyle.checks.naming;

import java.util.List;

public interface InputInterfaceTypeParameterName <it> {

}

interface OtherInterface <foo>{
	void action();
}

interface ThirdInterface <A>{
	void action2();
}

class OuterClass <Aaa> {
	interface InnerInterface<Taa extends List, Vaa> {
	}
}