package br.com.totvs.sistemaescolar.core.turma.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.totvs.tjf.core.validation.ValidatorService;

import br.com.totvs.sistemaescolar.core.turma.application.TurmaApplicationService;
import br.com.totvs.sistemaescolar.core.turma.domain.model.TurmaId;
import br.com.totvs.sistemaescolar.core.turma.exception.CriarTurmaException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/turmas/")
public class TurmaController {
	
	@Autowired
	private TurmaApplicationService service;

	@Autowired
	private ValidatorService validator;

	@ApiOperation(value = "API para adicionar uma turma", httpMethod = "POST", consumes = APPLICATION_JSON_VALUE)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Turma criada."), })

	@PostMapping
	@RequestMapping("adicionar")
	public ResponseEntity<Void> adicionar(@Valid @RequestBody CriarTurmaCommandDto turmaDto,
			BindingResult result) {

		validator.validate(turmaDto).ifPresent( violations -> { 
			throw new CriarTurmaException(violations); 
		});
			
		var cmd = CriarTurmaCommand.of(
				TurmaId.generate(),
				turmaDto.getDescricao(),
				turmaDto.getAnoLetivo(),
				turmaDto.getPeriodoLetivo(),
				turmaDto.getNumeroVagas(),
				turmaDto.getAlunoId(),
				turmaDto.getDisciplinaId());
		TurmaId id = service.handle(cmd);
					
		return ResponseEntity.created(
				ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/")
				.path(id.toString())
				.build().toUri())
				.build();
	}
	

}
