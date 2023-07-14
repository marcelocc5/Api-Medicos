package med.voll.api.consulta;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import med.voll.api.consulta.validacoes.ValidadorAgendamentoDeConsultas;
import med.voll.api.domain.ValidacaoException;
import med.voll.api.medico.Medico;
import med.voll.api.medico.MedicoRepository;
import med.voll.api.paciente.PacienteRepository;

@Service
public class AgendaDeConsultas {
	
	@Autowired
	private ConsultaRepository consultaRepository;
	
	@Autowired
	private MedicoRepository medicoRepository;
	
	@Autowired
	private PacienteRepository pacienteRepository;
	
	@Autowired
	private List<ValidadorAgendamentoDeConsultas> validadores;

	public DadosDetalhamentoConsulta agendar(DadosAgendamentoConsulta dados) {
		
		if(!pacienteRepository.existsById(dados.idPaciente())){
			throw new ValidacaoException("Id do paciente informado nao existe!");
		}
		if(dados.idMedico()!=null &&  !medicoRepository.existsById(dados.idMedico())){
			throw new ValidacaoException("Id do medico informado nao existe!");
		}
		
		validadores.forEach(v -> v.validar(dados));
		
		var medico = escolherMedico(dados);
		if(medico == null) {
			throw new ValidacaoException("Nao existe medico disponivel nesta data");
		}
		var paciente = pacienteRepository.getReferenceById(dados.idPaciente());
		var consulta = new Consulta(null, medico, paciente, dados.data(), null);
		consultaRepository.save(consulta);
		return new DadosDetalhamentoConsulta(consulta);
	}

	private Medico escolherMedico(DadosAgendamentoConsulta dados) {
		if(dados.idMedico() != null) {
			return medicoRepository.getReferenceById(dados.idMedico());
		}
		if(dados.especialidade() == null) {
			throw new ValidacaoException("Especialidade é obrigatorio quando não for informado um medico");
		}
		
		return medicoRepository.escolherMedicoAleatorioLivreNaData(dados.especialidade(), dados.data());
	}
	public void cancelar(DadosCancelamentoConsulta dados) {
	    if (!consultaRepository.existsById(dados.idConsulta())) {
	        throw new ValidacaoException("Id da consulta informado não existe!");
	    }

	    var consulta = consultaRepository.getReferenceById(dados.idConsulta());
	    consulta.cancelar(dados.motivo());
	}
}
