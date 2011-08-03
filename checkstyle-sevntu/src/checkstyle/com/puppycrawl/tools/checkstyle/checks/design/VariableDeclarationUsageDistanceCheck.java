package com.puppycrawl.tools.checkstyle.checks.design;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import antlr.collections.ASTEnumeration;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class VariableDeclarationUsageDistanceCheck extends Check {

	private int allowedDistance;

	private String ignoreVariables;

	private boolean ignoreSimpleDeclaration;

	private int errorLine;

	private boolean variableMeet;

	public int getAllowedDistance() {
		return allowedDistance;
	}

	public void setAllowedDistance(int allowedDistance) {
		this.allowedDistance = allowedDistance;
	}

	public String getIgnoreVariables() {
		if (ignoreVariables == null) {
			ignoreVariables = "";
		}
		return ignoreVariables;
	}

	public void setIgnoreVariables(String ignoreVariables) {
		this.ignoreVariables = ignoreVariables;
	}

	public boolean isIgnoreSimpleDeclaration() {
		return ignoreSimpleDeclaration;
	}

	public void setIgnoreSimpleDeclaration(boolean ignoreSimpleDeclaration) {
		this.ignoreSimpleDeclaration = ignoreSimpleDeclaration;
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.VARIABLE_DEF, };
	}

	@Override
	public void visitToken(DetailAST aAST) {
		variableMeet = false;
		int parentType = aAST.getParent().getType();
		DetailAST nextSibling = aAST.getNextSibling();
		if (parentType != TokenTypes.OBJBLOCK && nextSibling != null && nextSibling.getType() == TokenTypes.SEMI) {
			DetailAST variable = aAST.findFirstToken(TokenTypes.IDENT);
			if (!isVariableMatchesPattern(variable.getText())) {
				int dist = calculateDistance(nextSibling, variable);
				if (variableMeet) {
					dist++;
					if (!isDistanceAllowed(dist) && dist > 0) {
						log(errorLine, "variable.declaration.usage.distance", variable.getText());
//						System.out.println("var = " + variable.getText() + "; dist = " + dist + "; error = " + errorLine);
					}
				}
			}
		}
	}

	private int calculateDistance(DetailAST ast, DetailAST variable) {
		int dist = 0;
		boolean errorLineWasFound = false;
		boolean variableFirstMeet = false;
		DetailAST nextSibling = ast;
		int variableNumInForBlock = 0;
		boolean forBlockMeet = false;
		List<DetailAST> exprWithVariableList = new ArrayList<DetailAST>();
		while (nextSibling != null && nextSibling.getType() != TokenTypes.RCURLY) {
			switch (nextSibling.getType()) {
			case TokenTypes.CASE_GROUP:
				break;
			case TokenTypes.FOR_INIT:
			case TokenTypes.FOR_CONDITION:
			case TokenTypes.FOR_ITERATOR:
			case TokenTypes.FOR_EACH_CLAUSE:
				forBlockMeet = true;
				if (isASTContainsElement(nextSibling, variable)) {
					variableNumInForBlock++;
				}
				break;
			default:
				if (nextSibling.getType() == TokenTypes.VARIABLE_DEF) {
					if (nextSibling.getLastChild().getType() == TokenTypes.IDENT && isIgnoreSimpleDeclaration()) {
						break;
					}
				}
				if (nextSibling.getFirstChild() != null) {
					if (isASTContainsElement(nextSibling, variable)) {
							exprWithVariableList.add(nextSibling);
						if (!errorLineWasFound) {
							errorLine = nextSibling.getLineNo();
							errorLineWasFound = true;
						}
						variableMeet = true;
						variableFirstMeet = true;
					} else {
						if (!variableFirstMeet) {
							dist++;
						}
					}
				}
			}
			nextSibling = nextSibling.getNextSibling();
		}
		
		if (forBlockMeet && variableNumInForBlock == 0) {
			dist++;
		}

		if (exprWithVariableList.size() != 0) {
			DetailAST blockWithVariable = exprWithVariableList.get(0);

			if (exprWithVariableList.size() == 1) {
				if (blockWithVariable.getType() != TokenTypes.VARIABLE_DEF
						&& blockWithVariable.getType() != TokenTypes.EXPR) {
					dist += calculateDistance(blockWithVariable.getFirstChild(), variable);
				}
			}
		} else {
			if (!variableFirstMeet) {
				dist = 0;
			}
		}
		return dist;
	}
	
	private boolean isASTContainsElement(DetailAST ast, DetailAST element) {
		boolean isASTContainsElement = false;
		ASTEnumeration astList = ast.findAllPartial(element);
		while (astList.hasMoreNodes()) {
			DetailAST astElement = (DetailAST) astList.nextNode();
			DetailAST astElementParent = astElement.getParent();
			while (astElementParent != null) {
				if (astElementParent.equals(ast) && astElementParent.getLineNo() == ast.getLineNo()) {
					isASTContainsElement = true;
					break;
				}
				astElementParent = astElementParent.getParent();
			}
		}
		return isASTContainsElement;
	}

	private boolean isDistanceAllowed(int distance) {
		boolean isPassed = true;
		if (distance > getAllowedDistance()) {
			isPassed = false;
		}
		return isPassed;
	}

	private boolean isVariableMatchesPattern(String variable) {
		Pattern pattern = Pattern.compile(getIgnoreVariables());
		Matcher matcher = pattern.matcher(variable);
		return matcher.matches();
	}
}
