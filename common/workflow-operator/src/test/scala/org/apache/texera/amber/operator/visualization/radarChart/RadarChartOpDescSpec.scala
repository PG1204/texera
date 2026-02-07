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

package org.apache.texera.amber.operator.visualization.radarChart

import org.scalatest.BeforeAndAfter
import org.scalatest.flatspec.AnyFlatSpec

class RadarChartOpDescSpec extends AnyFlatSpec with BeforeAndAfter {
  var opDesc: RadarChartOpDesc = _
  before {
    opDesc = new RadarChartOpDesc()
  }

  it should "throw assertion error if nameColumn is empty" in {
    assertThrows[AssertionError] {
      opDesc.manipulateTable()
    }
  }

  it should "throw assertion error if valueColumns is empty" in {
    opDesc.nameColumn = "name"
    assertThrows[AssertionError] {
      opDesc.manipulateTable()
    }
  }

  it should "generate Python code with correct imports and structure" in {
    opDesc.nameColumn = "entity"
    opDesc.valueColumns = List("metric1", "metric2", "metric3")
    opDesc.fillOpacity = 0.6

    val pythonCode = opDesc.generatePythonCode()

    // Check essential imports
    assert(pythonCode.contains("from pytexera import *"))
    assert(pythonCode.contains("import plotly.graph_objects as go"))
    assert(pythonCode.contains("import plotly.io"))
    assert(pythonCode.contains("import pandas as pd"))

    // Check class definition
    assert(pythonCode.contains("class ProcessTableOperator(UDFTableOperator)"))
    assert(pythonCode.contains("def process_table(self, table: Table, port: int)"))

    // Check error handling
    assert(pythonCode.contains("if table.empty:"))
    assert(pythonCode.contains("RadarChart is not available"))

    // Check data manipulation
    assert(pythonCode.contains("table.dropna"))
    assert(pythonCode.contains("pd.to_numeric"))

    // Check plotly figure creation
    assert(pythonCode.contains("fig = go.Figure()"))
    assert(pythonCode.contains("go.Scatterpolar"))
    assert(pythonCode.contains("fill='toself'"))

    // Check column names are included
    assert(pythonCode.contains("entity"))
    assert(pythonCode.contains("metric1"))
    assert(pythonCode.contains("metric2"))
    assert(pythonCode.contains("metric3"))

    // Check fillOpacity value
    assert(pythonCode.contains("0.6"))
  }
}
