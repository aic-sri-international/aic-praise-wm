/*
 * Copyright (c) 2018, SRI International
 * All rights reserved.
 * Licensed under the The BSD 3-Clause License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://opensource.org/licenses/BSD-3-Clause
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of the aic-praise nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.sri.ai.praisewm.payload;

import java.util.List;
import java.util.ArrayList;

import com.sri.ai.praise.inference.HOGMQueryResult;
import com.sri.ai.praise.inference.HOGMQueryRunner;

public class ExpressionResult {

	private String queryString;
	private long queryDuration;
	private List<String> answers;

	public ExpressionResult() {
		queryString = "";
		queryDuration = -1;
		answers = new ArrayList<>();
	}
	public ExpressionResult(String queryString, long queryDuration, List<String> answers) {
		this.answers = answers;
		this.queryString = queryString;
		this.queryDuration = queryDuration;
	}

	public static ExpressionResult FromHOGMResult(HOGMQueryRunner runner, HOGMQueryResult result) {
		List<String> answers = new ArrayList<>();
		if (result.getResult() != null)
			answers.add(runner.simplifyAnswer(result.getResult(), result.getQueryExpression()).toString());
		result.getErrors().stream().map(error -> answers.add("Error: " + error.getErrorMessage()));
		return new ExpressionResult(
			result.getQueryString(),
			result.getMillisecondsToCompute(),
			answers
			);
	}

	public long getQueryDuration() {
		return this.queryDuration;
	}

	public String getQueryString() {
		return this.queryString;
	}

	public List<String> getAnswers() {
		return this.answers;
	}

	public boolean hasErrors() {
		return this.answers.stream()
			.anyMatch(answer -> answer.startsWith("Error"));
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
}
