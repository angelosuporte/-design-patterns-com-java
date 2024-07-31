package com.aprendendo.design_patterns.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aprendendo.design_patterns.model.Cliente;
import com.aprendendo.design_patterns.model.ClienteRepository;
import com.aprendendo.design_patterns.model.Endereco;
import com.aprendendo.design_patterns.model.EnderecoRepository;
import com.aprendendo.design_patterns.service.ClienteService;
import com.aprendendo.design_patterns.service.ViaCepService;

@Service
public class ClienteServiceImpl implements ClienteService {
	
	// TODO Singleton: Injetar componentes do Spring com @Autowired.
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private ViaCepService viaCepService;
	// TODO Strategy: Implementar metodos definidos na interface.
	// TODO Facade: Abstrair integracao com subsistemas(por API), provendo interface
	// simples.

	@Override
	public Iterable<Cliente> buscarTodos() {
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		Optional<Cliente> cliente = clienteRepository.findById(id);
		return cliente.get();
	}

	@Override
	public void inserir(Cliente cliente) {
		salvarClienteComCep(cliente);
	}

	@Override
	public void atualizar(Long id, Cliente cliente) {
		Optional<Cliente> clienteBD = clienteRepository.findById(id);
		if(clienteBD.isPresent()) {
			salvarClienteComCep(cliente);
		}

	}

	@Override
	public void deletar(Long id) {
		clienteRepository.deleteById(id);

	}
	
	private void salvarClienteComCep(Cliente cliente) {
		// verificar se o endereco do cliente ja existe no cep
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// Caso nao exista, integrar com o viaCep e fazer persistencia com o retorno
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando ao Endereco(novo ou existente).
		clienteRepository.save(cliente);
	}

}
