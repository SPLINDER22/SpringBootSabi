# 📸 INTEGRAR CLOUDINARY EN RAILWAY - GUÍA RÁPIDA

## 🎯 ¿Qué es Cloudinary?

Cloudinary es un servicio para almacenar imágenes en la nube. Lo necesitas porque **Railway borra los archivos** cada vez que redespliegas la aplicación.

---

## ⏱️ TIEMPO TOTAL: 10 minutos

---

## 📍 PASO 1: Crear Cuenta en Cloudinary (3 minutos)

### 1.1 Registrarse
1. Ve a: **https://cloudinary.com/users/register/free**
2. Llena el formulario:
   - Nombre
   - Email
   - Contraseña
   - Acepta términos
3. Click en **Create Account**

### 1.2 Verificar Email
1. Ve a tu email
2. Busca el email de Cloudinary
3. Click en **Verify your account**

### 1.3 Acceder al Dashboard
1. Una vez verificado, te redirigirá al Dashboard
2. Deberías ver algo como esto:

```
┌─────────────────────────────────────────┐
│  Product Environment Credentials        │
├─────────────────────────────────────────┤
│  Cloud name:    dxxxxxxxxx              │
│  API Key:       123456789012345         │
│  API Secret:    abcdefghijklmnopqrst    │
│                                          │
│  API Base URL:                          │
│  https://api.cloudinary.com/v1_1/dxxxx  │
└─────────────────────────────────────────┘
```

---

## 📍 PASO 2: Copiar tus Credenciales (1 minuto)

### 2.1 Copia estos 3 valores desde el Dashboard:

```
Cloud name: _________________
API Key: ____________________
API Secret: _________________
```

**💡 TIP**: Haz click en el ícono del "ojo" 👁️ junto a "API Secret" para revelarlo.

---

## 📍 PASO 3: Configurar en Railway (2 minutos)

### 3.1 Ir a Railway
1. Abre: **https://railway.app/dashboard**
2. Click en tu proyecto **springbootsabi-production**
3. Click en tu servicio **Spring Boot** (donde está tu código)

### 3.2 Agregar Variables de Cloudinary
1. Click en **Variables** (pestaña lateral)
2. Click en **+ New Variable** (3 veces, una por cada variable)

**Agrega estas 3 variables** (copia exactamente los nombres):

```
CLOUDINARY_CLOUD_NAME = [pega tu cloud name aquí]
CLOUDINARY_API_KEY = [pega tu api key aquí]
CLOUDINARY_API_SECRET = [pega tu api secret aquí]
```

**IMPORTANTE**: 
- ✅ Los nombres deben ser **EXACTAMENTE** como están escritos arriba
- ✅ No agregues espacios antes ni después
- ✅ No agregues comillas

### 3.3 Verificar
Deberías ver algo así en tu lista de variables:

```
✓ CLOUDINARY_CLOUD_NAME = dxxxxxxxxx
✓ CLOUDINARY_API_KEY = 123456789012345
✓ CLOUDINARY_API_SECRET = abcdefghijklmnopqrst
✓ MYSQLHOST = mysql.railway.internal
✓ MYSQLPORT = 3306
... (otras variables)
```

---

## 📍 PASO 4: El Código Ya Está Configurado ✅

**¡No necesitas hacer nada más en el código!** 

La aplicación ya está configurada para usar Cloudinary automáticamente cuando detecta estas variables.

### ¿Cómo funciona?

Tu archivo `DataSourceConfig.java` ya tiene este código:

```java
@Bean
public Cloudinary cloudinary() {
    String cloudName = System.getenv("CLOUDINARY_CLOUD_NAME");
    String apiKey = System.getenv("CLOUDINARY_API_KEY");
    String apiSecret = System.getenv("CLOUDINARY_API_SECRET");
    
    if (cloudName != null && apiKey != null && apiSecret != null) {
        return new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret,
            "secure", true
        ));
    }
    return null; // Fallback: guarda archivos localmente (temporal)
}
```

---

## 📍 PASO 5: Redesplegar y Verificar (2 minutos)

### 5.1 Forzar Redespliegue
Railway debería redesplegar automáticamente al agregar las variables, pero para asegurarte:

1. En tu servicio Spring Boot en Railway
2. Click en **Settings**
3. Scroll hasta **Service**
4. Click en **Redeploy**

### 5.2 Verificar en los Logs

1. Ve a **Deployments** (pestaña lateral)
2. Click en el deployment más reciente
3. Click en **View Logs**

**Busca este mensaje en los logs:**

```
✅ Cloudinary configured successfully!
   Cloud Name: dxxxxxxxxx
```

**O este si aún no agregaste las variables:**

```
⚠️ Cloudinary not configured - using local storage (temporary)
```

---

## 📍 PASO 6: Probar que Funciona (2 minutos)

### 6.1 Sube una Imagen de Prueba

1. Abre tu aplicación: `https://springbootsabi-production.up.railway.app`
2. Inicia sesión como administrador
3. Ve a una sección donde puedas subir imágenes:
   - **Perfil de usuario** → cambiar foto de perfil
   - **Crear ejercicio** → subir imagen/video
   - **Diagnóstico** → subir fotos

4. Sube una imagen de prueba

### 6.2 Verificar en Cloudinary

1. Ve al Dashboard de Cloudinary
2. Click en **Media Library** (menú lateral)
3. Deberías ver tu imagen recién subida

**Estructura de carpetas en Cloudinary:**
```
sabi/
├── perfiles/           (fotos de perfil)
├── diagnosticos/       (fotos de diagnósticos)
├── ejercicios/         (imágenes/videos de ejercicios)
└── otros/              (otros archivos)
```

### 6.3 Verificar en tu App

1. Refresca la página de tu app
2. La imagen debería verse correctamente
3. Inspecciona el HTML (F12 → Elements)
4. Busca la URL de la imagen, debería ser algo como:

```html
<img src="https://res.cloudinary.com/dxxxxxxxxx/image/upload/v1234567890/sabi/perfiles/abcd1234.jpg">
```

✅ **Si ves una URL de Cloudinary** → ¡Funciona perfectamente!
❌ **Si ves `/uploads/...`** → Las variables no están configuradas correctamente

---

## 🎯 Checklist Final

Marca todo lo que completaste:

- [ ] Cuenta de Cloudinary creada
- [ ] Email verificado
- [ ] Copié las 3 credenciales (cloud_name, api_key, api_secret)
- [ ] Agregué las 3 variables en Railway
- [ ] Los nombres de las variables son exactos: `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`
- [ ] Redespliegue completado
- [ ] Los logs muestran "✅ Cloudinary configured successfully!"
- [ ] Subí una imagen de prueba
- [ ] La imagen aparece en Cloudinary Media Library
- [ ] La URL de la imagen en mi app es de Cloudinary (res.cloudinary.com)

---

## 🔍 Troubleshooting

### ❌ Error: "Cloudinary not configured"

**Causa**: Las variables no están configuradas correctamente.

**Solución**:
1. Ve a Railway → Variables
2. Verifica que los nombres sean **EXACTAMENTE**:
   - `CLOUDINARY_CLOUD_NAME` (no `CLOUD_NAME`)
   - `CLOUDINARY_API_KEY` (no `API_KEY`)
   - `CLOUDINARY_API_SECRET` (no `API_SECRET`)
3. Verifica que no haya espacios antes/después de los valores
4. Redesplega

### ❌ Error: "Invalid API credentials"

**Causa**: Copiaste mal las credenciales.

**Solución**:
1. Ve al Dashboard de Cloudinary
2. Copia de nuevo cada valor con cuidado
3. Actualiza las variables en Railway
4. Redesplega

### ❌ Las imágenes NO se ven después de redesplegar

**Causa**: Las imágenes viejas estaban en almacenamiento local (se borraron).

**Solución**:
- ✅ Las imágenes subidas **DESPUÉS** de configurar Cloudinary estarán seguras
- ❌ Las imágenes subidas **ANTES** se perdieron (es normal)
- 💡 Vuelve a subir las imágenes importantes

### ❌ Error: "Upload failed"

**Causa**: Límite de almacenamiento excedido (raro en plan gratuito).

**Solución**:
1. Ve a Cloudinary Dashboard
2. Check **Usage** (menú lateral)
3. El plan gratuito incluye:
   - 25 GB de almacenamiento
   - 25 GB de ancho de banda/mes
   - 25,000 transformaciones/mes
4. Si excediste, considera:
   - Eliminar imágenes no usadas
   - Upgrade a plan pagado ($0.01/GB extra)

---

## 💰 Límites del Plan Gratuito

El plan gratuito de Cloudinary incluye:

| Recurso | Límite | ¿Es suficiente? |
|---------|--------|-----------------|
| Almacenamiento | 25 GB | ✅ Sí, para 25,000+ imágenes |
| Ancho de banda | 25 GB/mes | ✅ Sí, ~50,000 visitas/mes |
| Transformaciones | 25,000/mes | ✅ Sí, suficiente |
| Usuarios | Ilimitados | ✅ Sí |

**¿Cuándo necesitarías pagar?**
- Si subes miles de videos HD
- Si tienes +100,000 visitas/mes
- Si necesitas funciones avanzadas (face detection, etc.)

---

## 🎓 Aprende Más

### URLs de Cloudinary

Todas las imágenes tendrán URLs como:

```
https://res.cloudinary.com/TU_CLOUD_NAME/image/upload/v1234567890/sabi/carpeta/archivo.jpg
```

**Transformaciones automáticas:**

```
/w_300,h_300,c_fill/  → Redimensiona a 300x300
/q_auto/               → Calidad automática
/f_auto/               → Formato automático (WebP si es compatible)
```

### Ejemplo de uso en tu app:

En los templates Thymeleaf verás:

```html
<img th:src="@{${usuario.fotoPerfil}}" 
     alt="Foto de perfil"
     class="rounded-circle">
```

Si `usuario.fotoPerfil` es:
- `https://res.cloudinary.com/...` → Se usa tal cual
- `/uploads/...` → Se convierte a URL local (temporal)

---

## 📞 Soporte

**¿Problemas?**
1. Revisa el **Troubleshooting** arriba
2. Verifica los **logs de Railway**
3. Verifica el **Dashboard de Cloudinary**

---

## ✅ ¡Listo!

Una vez que completes todos los pasos:

1. ✅ MySQL funcionando
2. ✅ Cloudinary configurado
3. ✅ App desplegada en Railway

**Tu app está 100% funcional en producción** 🎉

---

## 🔗 Links Útiles

- **Dashboard Cloudinary**: https://cloudinary.com/console
- **Dashboard Railway**: https://railway.app/dashboard
- **Tu App**: https://springbootsabi-production.up.railway.app
- **Documentación Cloudinary**: https://cloudinary.com/documentation/java_integration

---

**¿Completaste todo?** → ¡Marca el checklist arriba! ☑️

