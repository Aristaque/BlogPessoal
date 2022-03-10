package org.generation.blogPessoal.service;

import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.generation.blogPessoal.model.UserLogin;
import org.generation.blogPessoal.model.Usuario;
import org.generation.blogPessoal.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
//
@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;

	public Optional<Usuario> CadastrarUsuario(Usuario usuario) {

		Optional<Usuario> usuarioM = repository.findByUsuario(usuario.getUsuario());

		if (usuarioM.isPresent()) {

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email ja cadastrado");

		} else {

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			String passwordEncoder = encoder.encode(usuario.getSenha());
			usuario.setSenha(passwordEncoder);

			return Optional.ofNullable(repository.save(usuario));
		}

	}

	public Optional<UserLogin> Logar(Optional<UserLogin> user) {
		BCryptPasswordEncoder enconder = new BCryptPasswordEncoder();
		Optional<Usuario> usuario = repository.findByUsuario(user.get().getUsuario());

		if (usuario.isPresent()) {
			if (enconder.matches(user.get().getSenha(), usuario.get().getSenha())) {

				String auth = user.get().getUsuario() + ":" + user.get().getSenha();
				byte[] encondedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic" + new String(encondedAuth);

				user.get().setToken(authHeader);
				user.get().setNome(usuario.get().getNome());

				return user;

			}
		}
		return null;

	}

}
