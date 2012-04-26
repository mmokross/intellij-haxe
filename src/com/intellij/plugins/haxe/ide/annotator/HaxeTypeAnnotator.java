package com.intellij.plugins.haxe.ide.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.plugins.haxe.HaxeBundle;
import com.intellij.plugins.haxe.ide.actions.HaxeTypeAddImportIntentionAction;
import com.intellij.plugins.haxe.ide.index.HaxeComponentIndex;
import com.intellij.plugins.haxe.lang.psi.*;
import com.intellij.plugins.haxe.util.HaxeResolveUtil;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author: Fedor.Korotkov
 */
public class HaxeTypeAnnotator extends HaxeVisitor implements Annotator {
  private AnnotationHolder myHolder = null;

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    assert myHolder == null;
    myHolder = holder;
    try {
      element.accept(this);
    }
    finally {
      myHolder = null;
    }
  }

  @Override
  public void visitType(@NotNull HaxeType type) {
    super.visitType(type);
    final HaxeClass haxeClassInType = HaxeResolveUtil.tryResolveClassByQName(type);
    if(haxeClassInType != null) {
      return;
    }

    final List<HaxeComponent> components = HaxeComponentIndex.getItemsByName(type.getExpression().getText(), type.getProject());
    if(!components.isEmpty()){
      myHolder.createErrorAnnotation(type, HaxeBundle.message("haxe.unresolved.type")).registerFix(new HaxeTypeAddImportIntentionAction(type, components));
    }
  }
}