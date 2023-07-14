package med.voll.api.consulta.validacoes;

import java.time.DayOfWeek;

import org.springframework.stereotype.Component;

import med.voll.api.consulta.DadosAgendamentoConsulta;
import med.voll.api.domain.ValidacaoException;

@Component
public class ValidadorHorarioFuncionamentoClinica implements ValidadorAgendamentoDeConsultas{
	
	public void validar(DadosAgendamentoConsulta dados) {
		var dataConsulta = dados.data();
		
		var domingo = dataConsulta.getDayOfWeek().equals(DayOfWeek.SUNDAY);
		var antesDaAberturaDaClinica = dataConsulta.getHour() <7;
		var depoisDoFechamentoDaClinica = dataConsulta.getHour() >18;
		
		if (domingo || antesDaAberturaDaClinica || depoisDoFechamentoDaClinica) {
			throw new ValidacaoException("Consulta fora do horario de funcionamento da clinica");
		}
	}

}
