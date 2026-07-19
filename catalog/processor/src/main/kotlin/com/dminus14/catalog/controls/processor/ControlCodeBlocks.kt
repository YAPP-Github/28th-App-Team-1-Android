package com.dminus14.catalog.controls.processor

import com.squareup.kotlinpoet.CodeBlock

/**
 * [adapter]의 매개변수 순서대로 Runtime Control 호출 코드를 현재 [CodeBlock.Builder]에 추가한다.
 *
 * 각 Control callback은 구체 타입 Args의 `copy`를 호출한다. 숫자는 화면에 보이는 입력 원문과 마지막
 * 유효 Args를 분리하고, enum은 실제 enum 타입의 `entries`를 사용하므로 reflection이나 `Any?` 기반
 * 변환이 필요하지 않다.
 */
internal fun CodeBlock.Builder.addControls(adapter: AdapterModel): CodeBlock.Builder {
    adapter.parameters.forEachIndexed { index, parameter ->
        when (val kind = parameter.kind) {
            ParameterKind.StringKind -> addTextControl(parameter)
            ParameterKind.BooleanKind -> addBooleanControl(parameter)
            is ParameterKind.NumberKind -> addNumberControl(index, parameter, kind)
            is ParameterKind.EnumKind -> addEnumControl(parameter)
        }
    }
    return this
}

private fun CodeBlock.Builder.addTextControl(parameter: ParameterModel) {
    add("%M(\n", CatalogControlsNames.textControl)
    indent()
    add("name = %S,\n", parameter.name)
    add("value = args.%N,\n", parameter.name)
    add("onValueChange = { args = args.copy(%N = it) },\n", parameter.name)
    unindent()
    add(")\n")
}

private fun CodeBlock.Builder.addBooleanControl(parameter: ParameterModel) {
    add("%M(\n", CatalogControlsNames.booleanControl)
    indent()
    add("name = %S,\n", parameter.name)
    add("value = args.%N,\n", parameter.name)
    add("onValueChange = { args = args.copy(%N = it) },\n", parameter.name)
    unindent()
    add(")\n")
}

private fun CodeBlock.Builder.addNumberControl(
    index: Int,
    parameter: ParameterModel,
    kind: ParameterKind.NumberKind,
) {
    add("%M(\n", CatalogControlsNames.numberControl)
    indent()
    add("name = %S,\n", parameter.name)
    add("rawValue = numberInput%L,\n", index)
    add("errorMessage = numberError%L,\n", index)
    add("onValueChange = { newValue ->\n")
    indent()
    add("numberInput%L = newValue\n", index)
    add("val parsed = %M(newValue)\n", kind.type.parser)
    add("numberError%L = parsed.errorMessage\n", index)
    add("parsed.value?.let { parsedValue ->\n")
    indent()
    add("args = args.copy(%N = parsedValue)\n", parameter.name)
    unindent()
    add("}\n")
    unindent()
    add("},\n")
    unindent()
    add(")\n")
}

private fun CodeBlock.Builder.addEnumControl(parameter: ParameterModel) {
    add("%M(\n", CatalogControlsNames.enumControl)
    indent()
    add("name = %S,\n", parameter.name)
    add("value = args.%N,\n", parameter.name)
    add("options = %T.entries.toList(),\n", parameter.typeName)
    add("onValueChange = { args = args.copy(%N = it) },\n", parameter.name)
    unindent()
    add(")\n")
}
