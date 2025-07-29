/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.tienda.controller;

/**
 *
 * @author Jose Sequeira
 */
import com.tienda.domain.Usuario;
import com.tienda.service.UsuarioService;
import com.tienda.service.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;



@Controller // Asigna que va a ser un controller
@RequestMapping("/usuario") // Asigna que va a ser un controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService; // Inyeccion de Usuario Service

    @Autowired
    private FirebaseStorageService firebaseStorageService; // Inyeccion de FireBase Service

    @GetMapping("/listado")
    public String listado(Model model) {
        var usuarios = usuarioService.getUsuarios(); // Obtiene lista de usuarios
        model.addAttribute("usuarios", usuarios);    // La pasa a la vista
        model.addAttribute("totalUsuarios", usuarios.size()); // También la cantidad
        return "usuario/listado"; // Returna la pagina de listado 
    }

    @GetMapping("/nuevo")
    public String usuarioNuevo(Usuario usuario) {
        return "usuario/modifica";
    }

    @PostMapping("/guardar")
    public String usuarioGuardar(Usuario usuario,
            @RequestParam("imagenFile") MultipartFile imagenFile) {
        if (!imagenFile.isEmpty()) { 
            usuarioService.save(usuario,false); 
            usuario.setRutaImagen(
                firebaseStorageService.cargarImagen(
                    imagenFile,
                    "usuario",
                    usuario.getIdUsuario()));
        }
        usuarioService.save(usuario,true); 
        return "redirect:/usuario/listado";
    }

    @GetMapping("/eliminar/{idUsuario}")
    public String usuarioEliminar(Usuario usuario) {
        usuarioService.delete(usuario); 
        return "redirect:/usuario/listado";
    }

    @GetMapping("/modificar/{idUsuario}")
    public String usuarioModificar(Usuario usuario, Model model) {
        usuario = usuarioService.getUsuario(usuario); 
        model.addAttribute("usuario", usuario); 
        return "/usuario/modifica"; 
    }
}