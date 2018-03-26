package com.sri.ai.praisewm.controller;

import com.sri.ai.praisewm.payload.ExpressionResult;
import com.sri.ai.praisewm.payload.ModelQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient(timeout = "36000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ExpressionControllerTests {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void testSolve() {
		ModelQuery mq = new ModelQuery(String.join("\n",
				"random terrorAttacks : 0..20;\n",
				"random newJobs : 0..100000; // 100K\n",
				"random dow: 11000..18000;\n",
				"random economyIsPoor : Boolean;\n",
				"random economyIsGreat : Boolean;\n",
				"random attackPerception: Boolean;\n",
				"random likeIncumbent  : 0..100000000; // 100M\n",
				"random likeChallenger : 0..100000000; // 100M\n",
				"//\n",
				"// P(terrorAttacks) = 1/21; // uniform\n",
				"//\n",
				"// P(newJobs) = 1/(100000 + 1); // uniform\n",
				"//\n",
				"// P(dow) = 1/(18000 - 11000 + 1); // uniform\n",
				"//\n",
				"economyIsPoor <=> dow < 13000 and newJobs < 30000;\n",
				"//\n",
				"economyIsGreat <=> dow > 16000 and newJobs > 70000;\n",
				"//\n",
				"attackPerception <=> terrorAttacks > 4;\n",
				"//\n",
				"// P(likeIncumbent) =\n",
				"if economyIsGreat\n",
				"  then if likeIncumbent > 70000000 then 0.9/30000000 else 0.1/(70000000 + 1)\n",
				"else if economyIsPoor\n",
				"  then if likeIncumbent < 40000000 then 0.8/40000000 else 0.2/(60000000 + 1)\n",
				"else if attackPerception\n",
				"  then if likeIncumbent < 60000000 then 0.9/60000000 else 0.1/(40000000 + 1)\n",
				"else 1/(100000000 + 1); // uniform\n",
				"//\n",
				"// P(likeChallenger) = 1/(100000000 + 1); // uniform\n",
				"//\n",
				"// Evidence scenarios:\n",
				"//\n",
				"// great economy:\n",
				"// dow = 18000; newJobs = 80000;\n",
				"//\n",
				"// poor economy:\n",
				"// dow = 12000; newJobs = 10000;\n",
				"//\n",
				"// attacks:\n",
				"// terrorAttacks = 5;\n",
				"//\n",
				"// great economy and attacks:\n",
				"// dow = 18000; newJobs = 80000; terrorAttacks = 5;\n"),
			 "likeIncumbent > likeChallenger");
		List<ExpressionResult> results = webTestClient.post().uri("/solve")
			.contentType(MediaType.APPLICATION_JSON_UTF8)
			.accept(MediaType.APPLICATION_JSON_UTF8)
			.body(Mono.just(mq), ModelQuery.class)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
			.returnResult(ExpressionResult.class)
			.getResponseBody()
			.collectList()
			.block();

		assertThat(results).isNotEmpty();
		assertThat(results).allSatisfy(result -> assertThat(result.getAnswers()).isNotEmpty());
		assertThat(results).allSatisfy(result -> assertThat(result.hasErrors()).isFalse());
	}
}
