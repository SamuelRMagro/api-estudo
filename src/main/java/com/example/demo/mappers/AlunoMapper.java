package com.example.demo.mappers;

import com.example.demo.dto.aluno.AlunoCreateDTO;
import com.example.demo.dto.aluno.AlunoResponseDTO;
import com.example.demo.dto.aluno.AlunoUpdateDTO;
import com.example.demo.dto.usuario.UsuarioCreateDTO;
import com.example.demo.dto.usuario.UsuarioUpdateDTO;
import com.example.demo.model.Aluno;
import com.example.demo.model.Usuario;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.Period;

@Mapper(componentModel = "spring")
public interface AlunoMapper {

    @Mapping(target = "usuario", source = "entity.usuario")
    @Mapping(target = "usuario.cpf", expression = "java(cpfMascarado(usuario))")
    AlunoResponseDTO fromEntityToDTO(Aluno entity);

    default String cpfMascarado(Usuario usuario) {
        return Usuario.cpfMascarado(usuario.getCpf());
    }

    @Mapping(target = "usuario.nome", source = "alunoCreateDTO.nome")
    @Mapping(target = "usuario.idade", expression = "java(calculaIdade(alunoCreateDTO.dataNascimento()))")
    @Mapping(target = "usuario.login", source = "alunoCreateDTO.login")
    @Mapping(target = "usuario.email", source = "alunoCreateDTO.email")
    @Mapping(target = "usuario.cpf", source = "alunoCreateDTO.cpf")
    @Mapping(target = "usuario.senha", source = "alunoCreateDTO.senha")
    @Mapping(target = "usuario.isAtivo", source = "alunoCreateDTO.isAtivo")
    Aluno fromCreateDtoToEntity(@Valid AlunoCreateDTO alunoCreateDTO);

    @Mapping(target = "idade", expression = "java(calculaIdade(alunoCreateDTO.dataNascimento()))")
    UsuarioCreateDTO fromAlunoCreateToUserCreate(AlunoCreateDTO alunoCreateDTO);

    @Mapping(target = "idade", expression = "java(calculaIdade(alunoUpdateDTO.dataNascimento()))")
    UsuarioUpdateDTO fromAlunoUpdateToUserUpdate(AlunoUpdateDTO alunoUpdateDTO);

    default Integer calculaIdade(LocalDate dataNascimento) {
        LocalDate hoje = LocalDate.now();

        return Period.between(dataNascimento, hoje).getYears();
    }
}
