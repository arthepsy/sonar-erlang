/*
 * SonarQube Erlang Plugin
 * Copyright © 2012-2018 Tamas Kende <kende.tamas@gmail.com>
 * Copyright © 2018 Denes Hegedus (Cursor Insight Ltd.) <hegedenes@cursorinsight.com>
 * Copyright © 2020 Andris Raugulis <moo@arthepsy.eu>
 * Copyright © 2021 Daniils Petrovs <dpetrovs@evolution.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sonar.erlang.checks;

import com.sonar.sslr.api.AstNode;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.erlang.parser.ErlangGrammarImpl;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.squidbridge.checks.SquidCheck;
import org.sonar.sslr.parser.LexerlessGrammar;

@Rule(key = "DoNotUseExportAll", priority = Priority.MINOR)
@BelongsToProfile(title = CheckList.REPOSITORY_NAME, priority = Priority.MAJOR)
@SqaleConstantRemediation("10min")
public class DoNotUseExportAllCheck extends SquidCheck<LexerlessGrammar> {

  @RuleProperty(key = "skipInFlowControl", defaultValue = "true",
    description = "Set it false if you want to check export_all in flow controls.")
  private boolean skipInFlowControl = true;

  @Override
  public void init() {
    subscribeTo(ErlangGrammarImpl.compileAttr);
  }

  @Override
  public void visitNode(AstNode node) {
    if (hasFlowControlParent(node) && "export_all".equalsIgnoreCase(node.getFirstChild(ErlangGrammarImpl.primaryExpression).getTokenOriginalValue())) {
      getContext().createLineViolation(this, "Do not use export_all", node);
    }
  }

  private boolean hasFlowControlParent(AstNode astNode) {
    return !astNode.hasAncestor(ErlangGrammarImpl.flowControlAttr) || !skipInFlowControl;
  }

  public void setsSkipInFlowControl(boolean skipInFlowControl) {
    this.skipInFlowControl = skipInFlowControl;
  }

}
