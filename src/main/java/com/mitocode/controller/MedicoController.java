package com.mitocode.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mitocode.exception.ModelNotFoundException;
import com.mitocode.model.Medico;
import com.mitocode.service.IMedicoService;


@RestController
@RequestMapping("/medicos") // parte de la buena practica que solo se pueda acceder como /pacientes
public class MedicoController {
	
	@Autowired
	private IMedicoService service;

	@GetMapping
	public ResponseEntity<List<Medico>> listar(){
		List<Medico> lista = service.listar();
		return new ResponseEntity<List<Medico>>(lista, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Medico> leerPorId(@PathVariable ("id") Integer id) {
		Medico obj = service.leerPorId(id); 
		if(obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}
		return new ResponseEntity<Medico>(obj, HttpStatus.OK);		
	}
	
	@GetMapping("/hateoas/{id}")
	public Resource<Medico> leerPorIdHateoas(@PathVariable ("id") Integer id) {
		Medico obj = service.leerPorId(id); 
		if(obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}

		Resource<Medico> 	resource = new Resource<Medico>(obj);
		
		ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).leerPorId(id));
		resource.add(linkTo.withRel("medico-Resource"));
		return resource;
	}
	
	@PostMapping
	public ResponseEntity<Object> registar(@Valid @RequestBody Medico pac) { //@RequestBody lee el json y lo representa en un objeto java
		Medico paciente = service.registrar(pac);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(paciente.getIdMedico()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@PutMapping
	public ResponseEntity<Object> modificar(@Valid @RequestBody Medico pac) {
		service.modificar(pac);
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> eliminar(@PathVariable("id") Integer id) {
		Medico obj = service.leerPorId(id);
		if(obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}else {
			service.eliminar(id);
		}
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

}
