/*
 * Copyright 2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenarc.rule.basic

import org.codenarc.rule.AbstractAstVisitor
import org.codenarc.rule.AbstractAstVisitorRule
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ReturnStatement
import org.codehaus.groovy.ast.stmt.ThrowStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.IfStatement
import org.codehaus.groovy.ast.expr.TernaryExpression

/**
 * Dead code appears after a return statement or an exception is thrown. If code appears after one of these statements then it will never be executed and can be safely deleted. 
 *
 * @author 'Hamlet D'Arcy'
 * @version $Revision: 24 $ - $Date: 2009-01-31 13:47:09 +0100 (Sat, 31 Jan 2009) $
 */
class DeadCodeRule extends AbstractAstVisitorRule {
    String name = 'DeadCode'
    int priority = 2
    Class astVisitorClass = DeadCodeAstVisitor
}

class DeadCodeAstVisitor extends AbstractAstVisitor {

    def void visitBlockStatement(BlockStatement block) {

        if (block.statements && block.statements.size() >= 2) {
            (block.statements.size() - 1).times {
                def statement = block.statements[it]
                if (statementForcesMethodReturn(statement)) {
                    addViolation block.statements[it+1]
                }
            }
        }
        super.visitBlockStatement block
    }

    private boolean statementForcesMethodReturn(Statement statement) {
        if (statement instanceof ReturnStatement ) { return true }
        if (statement instanceof ThrowStatement ) { return true }
        if (statement instanceof IfStatement) {
            return statementForcesMethodReturn(statement.ifBlock) && statementForcesMethodReturn(statement.elseBlock)
        }
        if (statement instanceof TernaryExpression) {
            return statementForcesMethodReturn(statement.trueExpression) && statementForcesMethodReturn(statement.falseExpression)
        }
        false
    }


}