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
package com.sri.ai.praisewm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Flux;

import com.sri.ai.praise.inference.HOGMQueryRunner;
import com.sri.ai.praise.lang.ModelLanguage;
import com.sri.ai.praise.model.common.io.ModelPage;
import com.sri.ai.praisewm.payload.ExpressionResult;
import com.sri.ai.praisewm.payload.ModelQuery;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ExpressionController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping(path = "/api/solve", produces = "application/json")
    public Flux<ExpressionResult> solveModel(@RequestBody ModelQuery modelQuery) {
		List<String> queries = new ArrayList<>();
		queries.add(modelQuery.getQuery());
		ModelPage modelPage = new ModelPage(ModelLanguage.HOGMv1, "REST API Model", modelQuery.getModel(), queries);
		HOGMQueryRunner queryRunner = new HOGMQueryRunner(modelPage.getModelString(), modelPage.getDefaultQueriesToRun());
		return Flux.fromStream(
			queryRunner.getResults().stream()
			.map(result -> ExpressionResult.FromHOGMResult(queryRunner, result)));
	}

	@ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(WebClientResponseException ex) {
        logger.error("Error from WebClient - Status {}, Body {}", ex.getRawStatusCode(),
                ex.getResponseBodyAsString(), ex);
        return ResponseEntity.status(ex.getRawStatusCode()).body(ex.getResponseBodyAsString());
    }
}
