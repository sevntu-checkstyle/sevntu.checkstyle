package com.puppycrawl.tools.checkstyle.checks.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import antlr.collections.ASTEnumeration;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class VariableDeclarationUsageDistanceCheck extends Check {

	private List<DetailAST> variableDefAndExprList;

	private List<DetailAST> variableDefList;

	private List<Integer> variableDefAndExprLineList;

	private int distance = 0;

	private String regExp;

	public int getDistance() {
		return distance;
	}

	public String getRegExp() {
		return regExp;
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.STATIC_INIT, TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF,
				TokenTypes.VARIABLE_DEF, TokenTypes.EXPR, TokenTypes.LITERAL_IF, TokenTypes.LITERAL_FOR,
				TokenTypes.LITERAL_WHILE, TokenTypes.LITERAL_SWITCH, };
	}

	@Override
	public void visitToken(DetailAST aAST) {
		int parentType = aAST.getParent().getType();
		switch (aAST.getType()) {
		case TokenTypes.VARIABLE_DEF:
			if (parentType != TokenTypes.OBJBLOCK && checkExprAndVariable(aAST)) {
				if (!variableDefList.contains(aAST)) {
					variableDefList.add(aAST);
				}
				variableDefAndExprList.add(aAST);
				variableDefAndExprLineList.add(aAST.getLineNo());
			}
			break;
		case TokenTypes.EXPR:
			if (parentType == TokenTypes.OBJBLOCK || !checkExprAndVariable(aAST)) {
				break;
			}
		case TokenTypes.LITERAL_IF:
		case TokenTypes.LITERAL_FOR:
		case TokenTypes.LITERAL_WHILE:
		case TokenTypes.LITERAL_SWITCH:
			variableDefAndExprList.add(aAST);
			variableDefAndExprLineList.add(aAST.getLineNo());
			break;
		default:
			if (parentType == TokenTypes.OBJBLOCK) {
				variableDefAndExprList = new ArrayList<DetailAST>();
				variableDefList = new ArrayList<DetailAST>();
				variableDefAndExprLineList = new ArrayList<Integer>();
			}
			break;
		}
	}

	@Override
	public void leaveToken(DetailAST aAST) {
		if (aAST.getType() == TokenTypes.STATIC_INIT || aAST.getType() == TokenTypes.CTOR_DEF
				|| aAST.getType() == TokenTypes.METHOD_DEF) {

			for (int i = 0; i < variableDefList.size(); i++) {
				DetailAST varDef = variableDefList.get(i);
				DetailAST variableIdent = varDef.findFirstToken(TokenTypes.IDENT);

				boolean variableFirstMeet = false;
				Map<Integer, Boolean> blocksOfVariableMeet = new HashMap<Integer, Boolean>();
				Map<Integer, Integer> blocksOfVariableDistance = new HashMap<Integer, Integer>();
				List<Integer> blocks = new ArrayList<Integer>();
				List<Integer> varNestingList = new ArrayList<Integer>();
				int varDefIndex = 0;

				for (int j = 0; j < variableDefAndExprList.size(); j++) {
					int key = variableDefAndExprList.get(j).getParent().getLineNo();
					if (!blocksOfVariableMeet.containsKey(key)) {
						blocks.add(key);
						blocksOfVariableMeet.put(key, false);
						blocksOfVariableDistance.put(key, 0);
						varNestingList.add(getVariableNesting(aAST, variableDefAndExprList.get(j)));
					}
					if (!variableFirstMeet && variableDefAndExprList.get(j).getLineNo() == varDef.getLineNo()) {
						variableFirstMeet = true;
						varDefIndex = j;
						continue;
					}
					if (variableFirstMeet) {
						int currentDistance = blocksOfVariableDistance.get(key);
						if (!blocksOfVariableMeet.get(key) && !blocksOfVariableMeet.containsValue(true)) {
							blocksOfVariableDistance.put(key, ++currentDistance);
						}
						if (isExprContainsVariable(variableDefAndExprList.get(j), variableIdent)) {
							blocksOfVariableMeet.put(key, true);
						}
					}
				}

				if (!blocksOfVariableMeet.containsValue(true)) {
					continue;
				}

				int rootVarNesting = varNestingList.get(0);
				int blockNumWithSimilarVar = 0;
				for (int k = 0; k < blocks.size(); k++) {
					int key = blocks.get(k);
					if (varNestingList.get(k) - rootVarNesting == 1 && blocksOfVariableMeet.get(key)) {
						blockNumWithSimilarVar++;
					}
				}

				int dist = 0;
				int index = 0;
				int block = blocks.get(index);

				if (blockNumWithSimilarVar <= 1) {
					do { // If variable is only in one internal block
						dist += blocksOfVariableDistance.get(block);
						if (blocksOfVariableMeet.get(block)) {
							break;
						}
						index++;
						block = blocks.get(index);
					} while (index < blocks.size());
				} else {
					// If variable is in more then one internal block
					while (!blocksOfVariableMeet.get(block) && index < blocks.size()) {
						dist += blocksOfVariableDistance.get(block);
						index++;
						block = blocks.get(index);
					}
				}
				if (!checkDistance(dist)) {
					log(variableDefAndExprLineList.get(varDefIndex + dist), "variable.declaration.usage.distance",
							variableIdent.getText());
//					System.out.println(variableIdent.getText() + ":" + variableDefAndExprLineList.get(varDefIndex + dist));
				}
			}
		}
	}

	private boolean isExprContainsVariable(DetailAST expr, DetailAST varIdent) {
		int lastExprLine;
		if (expr.getNextSibling() != null) {
			lastExprLine = expr.getNextSibling().getLineNo();
		} else {
			lastExprLine = expr.getLineNo();
		}
		boolean isExprContainsVar = false;
		ASTEnumeration astList = expr.findAllPartial(varIdent);
		while (astList.hasMoreNodes()) {
			DetailAST astElement = (DetailAST) astList.nextNode();
			if (astElement.getText().equals(varIdent.getText()) && astElement.getLineNo() <= lastExprLine) {
				isExprContainsVar = true;
			}
		}
		return isExprContainsVar;
	}

	private int getVariableNesting(DetailAST rootAST, DetailAST nestedAST) {
		int varNesting = -1;
		DetailAST parent = nestedAST.getParent();
		while (!rootAST.equals(parent)) {
			varNesting++;
			parent = parent.getParent();
		}
		return varNesting;
	}

	private boolean checkExprAndVariable(DetailAST exprOrVar) {
		boolean isPassedCheck = true;
		DetailAST parent = exprOrVar.getParent();
		while (parent.getType() != TokenTypes.OBJBLOCK) {
			if (parent.getType() == TokenTypes.LITERAL_IF || parent.getType() == TokenTypes.LITERAL_FOR
					|| parent.getType() == TokenTypes.LITERAL_WHILE || parent.getType() == TokenTypes.LITERAL_SWITCH
					|| parent.getType() == TokenTypes.VARIABLE_DEF || parent.getType() == TokenTypes.EXPR) {
				isPassedCheck = false;
				break;
			}
			parent = parent.getParent();
		}
		return isPassedCheck;
	}

	private boolean checkDistance(int distance) {
		boolean isPassed = true;
		if (distance > getDistance()) {
			isPassed = false;
		}
		return isPassed;
	}
}
