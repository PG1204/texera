/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.texera.amber.operator.huggingFace.codegen

import org.apache.texera.amber.pybuilder.PyStringTypes.EncodableString
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class QaRankingCodegenSpec extends AnyFlatSpec with Matchers {

  private def makeCtx(
      hfApiToken: EncodableString = "token",
      modelId: EncodableString = "deepset/roberta-base-squad2",
      promptColumn: EncodableString = "prompt",
      resultColumn: EncodableString = "hf_response",
      task: EncodableString = "question-answering",
      systemPrompt: EncodableString = "You are a helpful assistant.",
      safeMaxTokens: Int = 256,
      safeTemp: Double = 0.7
  ): CodegenContext =
    CodegenContext(
      hfApiToken = hfApiToken,
      modelId = modelId,
      promptColumn = promptColumn,
      resultColumn = resultColumn,
      task = task,
      systemPrompt = systemPrompt,
      safeMaxTokens = safeMaxTokens,
      safeTemp = safeTemp
    )

  "QaRankingCodegen.task" should "be the canonical question-answering string" in {
    QaRankingCodegen.task shouldBe "question-answering"
  }

  "QaRankingCodegen.tasks" should "cover exactly the five QA/ranking tasks" in {
    QaRankingCodegen.tasks shouldBe Set(
      "question-answering",
      "table-question-answering",
      "zero-shot-classification",
      "sentence-similarity",
      "text-ranking"
    )
    QaRankingCodegen.tasks should have size 5
  }

  "QaRankingCodegen.payloadPython" should "build a question/context payload for question-answering" in {
    val out = QaRankingCodegen.payloadPython(makeCtx())
    out should include("""if task == "question-answering":""")
    out should include("self.CONTEXT_COLUMN")
    out should include(""""question": prompt_value""")
    out should include(""""context": ctx_val""")
  }

  it should "route the table for table-question-answering" in {
    val out = QaRankingCodegen.payloadPython(makeCtx())
    out should include("""elif task == "table-question-answering":""")
    out should include(""""query": prompt_value""")
    out should include("table_dict")
  }

  it should "split candidate labels for zero-shot-classification" in {
    val out = QaRankingCodegen.payloadPython(makeCtx())
    out should include("""elif task == "zero-shot-classification":""")
    out should include("self.CANDIDATE_LABELS")
    out should include("candidate_labels")
  }

  it should "build sentence lists for sentence-similarity and text-ranking" in {
    val out = QaRankingCodegen.payloadPython(makeCtx())
    out should include("""elif task == "sentence-similarity":""")
    out should include("""elif task == "text-ranking":""")
    out should include("self.SENTENCES_COLUMN")
    out should include("source_sentence")
    out should include(""""query": prompt_value""")
  }

  it should "fall back to the raw prompt as inputs" in {
    val out = QaRankingCodegen.payloadPython(makeCtx())
    out should include("""payload = {"inputs": prompt_value}""")
  }

  "QaRankingCodegen.parsePython" should "extract the answer field for QA variants" in {
    val out = QaRankingCodegen.parsePython(makeCtx())
    out should include("""if task == "question-answering":""")
    out should include("""elif task == "table-question-answering":""")
    out should include("""body.get("answer", json.dumps(body))""")
  }

  it should "serialize the raw body for zero-shot / similarity / ranking" in {
    val out = QaRankingCodegen.parsePython(makeCtx())
    out should include(
      """elif task in ("zero-shot-classification", "sentence-similarity", "text-ranking"):"""
    )
    out should include("json.dumps(body)")
  }

  "QaRankingCodegen snippets" should "never inline raw CodegenContext string values" in {
    // The snippets are static and reference only self.* attributes; the base
    // class decodes user-supplied strings safely at runtime. Sentinel values
    // are distinctive and non-overlapping with the static template text.
    val ctx = makeCtx(
      hfApiToken = "MARKER_TOKEN_zXyq42",
      modelId = "MARKER_MODEL_zXyq42",
      promptColumn = "MARKER_PROMPT_zXyq42",
      resultColumn = "MARKER_RESULT_zXyq42",
      task = "MARKER_TASK_zXyq42",
      systemPrompt = "MARKER_SYSTEM_zXyq42"
    )
    val payload = QaRankingCodegen.payloadPython(ctx)
    val parse = QaRankingCodegen.parsePython(ctx)

    payload should not include "MARKER_TOKEN_zXyq42"
    payload should not include "MARKER_MODEL_zXyq42"
    payload should not include "MARKER_PROMPT_zXyq42"
    payload should not include "MARKER_RESULT_zXyq42"
    payload should not include "MARKER_TASK_zXyq42"
    payload should not include "MARKER_SYSTEM_zXyq42"
    parse should not include "MARKER_TOKEN_zXyq42"
    parse should not include "MARKER_MODEL_zXyq42"
    parse should not include "MARKER_PROMPT_zXyq42"
    parse should not include "MARKER_RESULT_zXyq42"
    parse should not include "MARKER_TASK_zXyq42"
    parse should not include "MARKER_SYSTEM_zXyq42"
  }

  it should "produce identical output regardless of the CodegenContext contents" in {
    // QA/ranking payload/parse are static — they reference only self.*
    // attributes, never ctx fields. Two unrelated contexts must serialise to
    // byte-identical Python. A future refactor that accidentally consumes a
    // ctx field will regress here.
    val ctxA = makeCtx(
      hfApiToken = "token-A",
      modelId = "model-A",
      promptColumn = "col-A",
      resultColumn = "result-A",
      systemPrompt = "system-A",
      safeMaxTokens = 1,
      safeTemp = 0.0
    )
    val ctxB = makeCtx(
      hfApiToken = "token-B",
      modelId = "model-B",
      promptColumn = "col-B",
      resultColumn = "result-B",
      systemPrompt = "system-B",
      safeMaxTokens = 4096,
      safeTemp = 2.0
    )

    QaRankingCodegen.payloadPython(ctxA) shouldBe QaRankingCodegen.payloadPython(ctxB)
    QaRankingCodegen.parsePython(ctxA) shouldBe QaRankingCodegen.parsePython(ctxB)
  }
}
