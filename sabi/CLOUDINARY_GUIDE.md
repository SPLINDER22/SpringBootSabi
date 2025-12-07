# 📸 GUÍA: IMPLEMENTAR CLOUDINARY EN SABI

## ¿Por qué Cloudinary?

Railway usa almacenamiento **efímero** en `/tmp`. Los archivos se borran al reiniciar.

**Cloudinary** es la solución más simple para imágenes:
- ✅ Plan gratuito generoso (25 GB)
- ✅ CDN global incluido
- ✅ Transformaciones automáticas
- ✅ Fácil integración con Spring Boot

---

## 🚀 PASO 1: Crear Cuenta Cloudinary

1. Ve a: https://cloudinary.com/users/register/free
2. Regístrate con email
3. Verifica tu email
4. Ve al Dashboard

---

## 🔑 PASO 2: Obtener Credenciales

En el Dashboard de Cloudinary, encontrarás:

```
Cloud name: tu_cloud_name
API Key: 123456789012345
API Secret: tu_api_secret_aqui
```

O copia directamente:
```
cloudinary://123456789012345:tu_api_secret@tu_cloud_name
```

---

## 📦 PASO 3: Agregar Dependencia

Edita `pom.xml`:

```xml
<!-- Cloudinary para almacenamiento de imágenes -->
<dependency>
    <groupId>com.cloudinary</groupId>
    <artifactId>cloudinary-http44</artifactId>
    <version>1.36.0</version>
</dependency>
```

---

## ⚙️ PASO 4: Configurar en application-prod.properties

```properties
# Cloudinary Configuration
cloudinary.url=${CLOUDINARY_URL}
```

---

## 🔧 PASO 5: Crear Servicio Cloudinary

Crea: `src/main/java/com/sabi/sabi/service/CloudinaryService.java`

```java
package com.sabi.sabi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(@Value("${cloudinary.url}") String cloudinaryUrl) {
        this.cloudinary = new Cloudinary(cloudinaryUrl);
        this.cloudinary.config.secure = true;
    }

    /**
     * Sube una imagen de perfil a Cloudinary
     */
    public String uploadImagenPerfil(MultipartFile file, Long userId) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "sabi/perfiles",
                        "public_id", "perfil_" + userId,
                        "overwrite", true,
                        "resource_type", "image",
                        "transformation", new com.cloudinary.Transformation()
                                .width(500).height(500).crop("limit")
                                .quality("auto")
                ));
        return uploadResult.get("secure_url").toString();
    }

    /**
     * Sube una imagen de diagnóstico a Cloudinary
     */
    public String uploadImagenDiagnostico(MultipartFile file, String tipo, Long clienteId) throws IOException {
        String publicId = String.format("diagnostico_%d_%s_%d", 
                clienteId, tipo, System.currentTimeMillis());
        
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "sabi/diagnosticos",
                        "public_id", publicId,
                        "resource_type", "image",
                        "transformation", new com.cloudinary.Transformation()
                                .width(1200).height(1200).crop("limit")
                                .quality("auto")
                ));
        return uploadResult.get("secure_url").toString();
    }

    /**
     * Elimina una imagen de Cloudinary
     */
    public void deleteImage(String imageUrl) {
        try {
            // Extraer public_id de la URL
            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar imagen: " + e.getMessage());
        }
    }

    private String extractPublicId(String imageUrl) {
        // URL ejemplo: https://res.cloudinary.com/cloud/image/upload/v123/sabi/perfiles/perfil_1.jpg
        if (imageUrl.contains("/upload/")) {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length > 1) {
                String path = parts[1];
                // Remover versión si existe
                if (path.startsWith("v")) {
                    path = path.substring(path.indexOf("/") + 1);
                }
                // Remover extensión
                return path.substring(0, path.lastIndexOf("."));
            }
        }
        return null;
    }
}
```

---

## 📝 PASO 6: Actualizar PerfilController

Modifica `PerfilController.java`:

```java
@Autowired
private CloudinaryService cloudinaryService;

@PostMapping("/actualizar-foto-perfil")
public String actualizarFotoPerfil(@RequestParam("fotoPerfil") MultipartFile fotoPerfil,
                                  Authentication authentication,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
    try {
        Usuario usuario = obtenerUsuarioAutenticado(authentication);
        
        if (fotoPerfil.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debes seleccionar una imagen");
            return "redirect:/perfil";
        }

        // Subir a Cloudinary
        String imageUrl = cloudinaryService.uploadImagenPerfil(fotoPerfil, usuario.getId());
        
        // Eliminar imagen anterior de Cloudinary si existe
        if (usuario.getFotoPerfil() != null && usuario.getFotoPerfil().contains("cloudinary")) {
            cloudinaryService.deleteImage(usuario.getFotoPerfil());
        }
        
        // Guardar URL en base de datos
        usuario.setFotoPerfil(imageUrl);
        usuarioRepository.save(usuario);
        
        redirectAttributes.addFlashAttribute("mensaje", "Foto de perfil actualizada correctamente");
        return "redirect:/perfil";
        
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error al actualizar foto: " + e.getMessage());
        return "redirect:/perfil";
    }
}
```

---

## 🩺 PASO 7: Actualizar DiagnosticoController

Modifica `DiagnosticoController.java`:

```java
@Autowired
private CloudinaryService cloudinaryService;

private String guardarImagenDiagnostico(MultipartFile imagen, String tipo, Long clienteId) throws IOException {
    if (imagen.isEmpty()) {
        return null;
    }
    
    // Subir a Cloudinary
    return cloudinaryService.uploadImagenDiagnostico(imagen, tipo, clienteId);
}
```

---

## 🔐 PASO 8: Configurar en Railway

En Railway → Variables, añade:

```bash
CLOUDINARY_URL=cloudinary://API_KEY:API_SECRET@CLOUD_NAME
```

Ejemplo real:
```bash
CLOUDINARY_URL=cloudinary://123456789012345:abcDEF123ghiJKL456@tu_cloud_name
```

---

## 🧪 PASO 9: Probar Localmente

```bash
# Agregar variable temporal
$env:CLOUDINARY_URL="cloudinary://tu_clave_aqui"

# Compilar
mvn clean package -DskipTests

# Ejecutar con perfil prod
java -Dspring.profiles.active=prod -jar target/sabi-0.0.1-SNAPSHOT.jar
```

Prueba subir una imagen de perfil.

---

## 📋 PASO 10: Desplegar en Railway

```bash
git add .
git commit -m "Implement Cloudinary for image storage"
git push origin main
```

Railway re-desplegará automáticamente.

---

## ✅ VERIFICAR FUNCIONAMIENTO

1. **Subir imagen de perfil**
2. **Verificar en Cloudinary Dashboard**: Media Library
3. **Reiniciar app en Railway**
4. **Verificar que imagen sigue visible** ✅

---

## 🎨 BONUS: Transformaciones

Cloudinary puede transformar imágenes automáticamente:

```java
// Imagen cuadrada de 300x300
.transformation(new Transformation()
    .width(300).height(300).crop("fill").gravity("face"))

// Imagen circular
.transformation(new Transformation()
    .width(300).height(300).crop("fill").gravity("face")
    .radius("max"))

// Imagen con marca de agua
.transformation(new Transformation()
    .overlay("logo_sabi")
    .gravity("south_east")
    .x(10).y(10))

// Imagen en blanco y negro
.transformation(new Transformation()
    .effect("grayscale"))
```

---

## 💰 LÍMITES DEL PLAN GRATUITO

**Plan Free de Cloudinary**:
- ✅ 25 créditos/mes
- ✅ 25 GB almacenamiento
- ✅ 25 GB bandwidth
- ✅ Transformaciones incluidas
- ✅ CDN global

**Suficiente para**:
- ~5,000 imágenes subidas/mes
- ~10,000 visitantes/mes

**Si necesitas más**: $99/mes (Plus plan)

---

## 🐛 TROUBLESHOOTING

### Error: "CLOUDINARY_URL not set"
```bash
# Verificar en Railway
railway variables

# Debe aparecer CLOUDINARY_URL
```

### Error: "Invalid API credentials"
```bash
# Verificar formato:
cloudinary://API_KEY:API_SECRET@CLOUD_NAME

# Sin espacios, sin comillas
```

### Imagen no se muestra
```java
// Verificar que la URL se guarda correctamente
System.out.println("URL Cloudinary: " + imageUrl);

// Debe ser algo como:
// https://res.cloudinary.com/tu_cloud/image/upload/v123/sabi/perfiles/perfil_1.jpg
```

---

## 📚 RECURSOS

- **Docs Cloudinary**: https://cloudinary.com/documentation
- **Java SDK**: https://cloudinary.com/documentation/java_integration
- **Transformaciones**: https://cloudinary.com/documentation/image_transformations
- **Precios**: https://cloudinary.com/pricing

---

## 🎯 RESUMEN

Con Cloudinary implementado:
- ✅ Imágenes **persistentes** (no se borran)
- ✅ **CDN** global (carga rápida)
- ✅ **Transformaciones** automáticas
- ✅ **Gratis** hasta 25 GB
- ✅ **Sin configuración** de servidor

**Tiempo estimado**: 30-45 minutos

---

**¡Tu problema de archivos efímeros está resuelto!** 🎉

