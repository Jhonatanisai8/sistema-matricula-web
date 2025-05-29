package com.isai.demowebregistrationsystem.model.dtos;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ApoderadoDTO {
    private Integer idApoderado;
    private String dniPersona;
    private String nombresPersona;
    private String apellidosPersona;
    private String parentesco;
    private String telefono;
    private String emailPersonal;
    private Boolean esPrincipal;
    private Boolean autorizadoRecoger;
}
