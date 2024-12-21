
package com.lab6.laboratorio6.controller;

import com.lab6.laboratorio6.model.Eventos;
import com.lab6.laboratorio6.service.EventoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

// Importaciones para PDF
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

// Importaciones para Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    @Autowired
    private EventoService service;

    // Listar eventos
    @GetMapping
    public String listarEventos(Model model) {
        model.addAttribute("eventos", service.listar());
        return "lista_eventos"; // Nombre de la vista para mostrar eventos
    }

    // Mostrar formulario para crear un nuevo evento
    @GetMapping("/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("evento", new Eventos());
        return "formularioEventos"; // Nombre de la vista del formulario
    }

    // Guardar un evento
    @PostMapping
    public String guardar(@ModelAttribute Eventos evento) {
        service.guardar(evento);
        return "redirect:/eventos";
    }

    // Eliminar evento por ID
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") int id) {
        service.eliminar(id);
        return "redirect:/eventos";
    }
    
    // Mostrar formulario para editar un evento existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") int id, Model model) {
        Eventos evento = service.obtenerPorId(id);
        if (evento != null) {
            model.addAttribute("evento", evento);
            return "formularioEventos"; // Reutilizamos la vista del formulario
        } else {
            return "redirect:/eventos"; // Redirigir si el evento no existe
        }
    }

    // Guardar los cambios del evento editado
    @PostMapping("/editar")
    public String guardarEdicion(@ModelAttribute Eventos evento) {
        service.guardar(evento); // El método guardar actualizará el registro existente
        return "redirect:/eventos";
    }

    // Exportar eventos a PDF
    @GetMapping("/pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=eventos.pdf");

        // Crear el documento PDF
        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer))) {
            document.add(new Paragraph("Reporte de Eventos").setBold().setFontSize(18));
            
            // Crear tabla
            Table table = new Table(4);
            table.addCell("ID");
            table.addCell("Título");
            table.addCell("Fecha");
            table.addCell("Lugar");
            
            // Validar si hay eventos
            if (service.listar() != null) {
                for (Eventos e : service.listar()) {
                    table.addCell(String.valueOf(e.getId()));
                    table.addCell(e.getTitulo());
                    table.addCell(e.getFecha().toString());
                    table.addCell(e.getLugar());
                }
            }
            
            document.add(table);
        } catch (Exception e) {
            e.printStackTrace(); // Agregar manejo de excepciones
        }
    }

    // Exportar eventos a Excel
    @GetMapping("/excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=eventos.xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Eventos");
            
            // Encabezados
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Título");
            header.createCell(2).setCellValue("Fecha");
            header.createCell(3).setCellValue("Lugar");
            
            // Validar si hay eventos
            if (service.listar() != null) {
                int rowIdx = 1;
                for (Eventos e : service.listar()) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(e.getId());
                    row.createCell(1).setCellValue(e.getTitulo());
                    row.createCell(2).setCellValue(e.getFecha().toString());
                    row.createCell(3).setCellValue(e.getLugar());
                }
            }
            
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace(); // Agregar manejo de excepciones
        }
    }
}