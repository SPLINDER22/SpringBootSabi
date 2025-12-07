# 📧 Configuración de Email y Gestión del Proyecto en Railway

## 🎯 PARTE 1: Configurar Envío de Correos en Railway

### Opción A: Usar Gmail (Recomendado para pruebas)

#### Paso 1: Crear una Contraseña de Aplicación en Gmail

1. Ve a tu cuenta de Gmail
2. Accede a: https://myaccount.google.com/security
3. Activa la **"Verificación en 2 pasos"** (si no la tienes)
4. Busca **"Contraseñas de aplicaciones"**
5. Click en **"Contraseñas de aplicaciones"**
6. Selecciona:
   - App: **Correo**
   - Dispositivo: **Otro** → Escribe "Railway SABI"
7. Click **"Generar"**
8. **COPIA LA CONTRASEÑA** (16 caracteres sin espacios)
   - Ejemplo: `abcd efgh ijkl mnop` → Copia: `abcdefghijklmnop`

#### Paso 2: Configurar Variables en Railway

1. Ve al **Railway Dashboard**
2. Click en tu servicio **springbootsabi**
3. Ve a la pestaña **Variables**
4. Click en **"+ New Variable"** para cada una:

```env
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=abcdefghijklmnop
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
```

**⚠️ IMPORTANTE:**
- Usa TU email de Gmail en `MAIL_USERNAME`
- Usa la contraseña de aplicación (16 caracteres) en `MAIL_PASSWORD`
- NO uses tu contraseña normal de Gmail

#### Paso 3: Verificar en los Logs

Después de configurar, el servicio se reiniciará automáticamente. 

Deberías ver en los logs:
```
✅ Email service configured
   Host: smtp.gmail.com
   Port: 587
   Username: tu_email@gmail.com
```

---

### Opción B: Usar SendGrid (Recomendado para producción)

SendGrid ofrece **100 emails gratis por día**, perfecto para tu aplicación.

#### Paso 1: Crear Cuenta en SendGrid

1. Ve a: https://sendgrid.com/
2. Click en **"Start for Free"**
3. Crea tu cuenta con email y contraseña
4. Confirma tu email

#### Paso 2: Crear API Key

1. Una vez dentro, ve a: **Settings** → **API Keys**
2. Click en **"Create API Key"**
3. Nombre: **"Railway SABI App"**
4. Permisos: **"Full Access"** o **"Mail Send"**
5. Click **"Create & View"**
6. **COPIA LA API KEY** (empieza con `SG.`)
   - Ejemplo: `SG.abcdef123456...`
7. **⚠️ Guárdala en un lugar seguro, NO la volverás a ver**

#### Paso 3: Verificar un Email de Remitente

SendGrid requiere que verifiques el email desde el que enviarás:

1. Ve a: **Settings** → **Sender Authentication**
2. Click en **"Verify a Single Sender"**
3. Llena el formulario:
   - From Name: **SABI - Sistema de Entrenamiento**
   - From Email Address: **tu_email@gmail.com**
   - Reply To: **tu_email@gmail.com**
   - Company: **SABI**
   - Address, City, State, Zip: (puedes poner datos de prueba)
   - Country: **Chile**
4. Click **"Create"**
5. **Revisa tu email** y haz click en el link de verificación

#### Paso 4: Configurar Variables en Railway

1. Ve al **Railway Dashboard** → **springbootsabi** → **Variables**
2. Agrega estas variables:

```env
MAIL_HOST=smtp.sendgrid.net
MAIL_PORT=587
MAIL_USERNAME=apikey
MAIL_PASSWORD=SG.tu_api_key_completa_aqui
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
SENDGRID_API_KEY=SG.tu_api_key_completa_aqui
```

**⚠️ IMPORTANTE:**
- `MAIL_USERNAME` debe ser exactamente: `apikey` (es literal, no cambies esto)
- `MAIL_PASSWORD` es tu API Key completa de SendGrid
- También agrega `SENDGRID_API_KEY` por si la usas directamente

---

### Opción C: Usar Brevo (ex-Sendinblue) - También Gratis

Brevo ofrece **300 emails gratis por día**.

#### Paso 1: Crear Cuenta

1. Ve a: https://www.brevo.com/
2. Click en **"Sign up free"**
3. Completa el registro y verifica tu email

#### Paso 2: Obtener SMTP Credentials

1. Ve a: **SMTP & API** → **SMTP**
2. Verás tus credenciales:
   - SMTP Server: `smtp-relay.brevo.com`
   - Port: `587`
   - Login: tu email registrado
   - Password: **Copia la clave SMTP**

#### Paso 3: Configurar en Railway

```env
MAIL_HOST=smtp-relay.brevo.com
MAIL_PORT=587
MAIL_USERNAME=tu_email@ejemplo.com
MAIL_PASSWORD=tu_clave_smtp_de_brevo
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
```

---

## 🔄 PARTE 2: Gestionar el Proyecto en Railway

### ⏸️ Cómo DETENER el Proyecto

#### Opción 1: Pausar el Servicio (Recomendado)
1. Ve al **Railway Dashboard**
2. Click en tu servicio **springbootsabi**
3. Ve a la pestaña **Settings**
4. Scroll hasta **"Service Settings"**
5. Click en **"Pause Service"**
6. Confirma

**✅ Ventajas:**
- No pierdes datos
- Puedes reactivarlo fácilmente
- No consume recursos ni créditos
- Las variables se mantienen

#### Opción 2: Eliminar el Servicio (⚠️ Cuidado)
1. Ve a **Settings**
2. Scroll hasta el final
3. Click en **"Delete Service"**
4. Escribe el nombre del servicio para confirmar

**⚠️ Advertencia:**
- Perderás TODA la configuración
- Tendrás que reconfigurarlo desde cero

---

### ▶️ Cómo REINICIAR el Proyecto

#### Si está Pausado:
1. Ve al servicio pausado
2. Click en **"Resume Service"**
3. Espera 1-2 minutos
4. Verifica los logs

#### Si está en Ejecución (reinicio forzado):

**Opción 1: Desde Railway Dashboard**
1. Ve a tu servicio **springbootsabi**
2. Click en los **3 puntos** (⋮) arriba a la derecha
3. Click en **"Restart"**
4. Espera a que se reinicie

**Opción 2: Redesplegar**
1. Ve a la pestaña **"Deployments"**
2. Click en el último deployment
3. Click en **"Redeploy"**

**Opción 3: Cambiar una Variable**
1. Ve a **Variables**
2. Cambia cualquier variable (ej: agrega un espacio)
3. Guarda
4. El servicio se reiniciará automáticamente
5. Vuelve a dejar la variable como estaba

**Opción 4: Hacer un Push a GitHub**
```bash
# En tu terminal local:
cd C:\Users\USER\Downloads\SpringBootSabi
git add .
git commit -m "Force redeploy"
git push origin main
```

Railway detectará el push y redesplegará automáticamente.

---

## 🔍 Verificar que el Email Funciona

### 1. Revisar los Logs

Después de configurar, busca en los logs:

```
📧 Email Configuration:
   Host: smtp.gmail.com (o el que uses)
   Port: 587
   Username: tu_email@gmail.com
   Authentication: enabled
   STARTTLS: enabled
✅ Email service configured successfully!
```

### 2. Probar el Envío

Dependiendo de tu aplicación, prueba:
- Registro de nuevo usuario
- Recuperación de contraseña
- Notificaciones del sistema

### 3. Si No Funciona

**Revisa los logs en Railway:**
```
# Busca errores como:
Authentication failed
Could not connect to SMTP host
Connection timeout
```

**Soluciones comunes:**
- Verifica que la contraseña sea correcta
- Asegúrate que `MAIL_PORT=587`
- Confirma que `MAIL_SMTP_STARTTLS=true`
- Revisa que el email esté verificado (SendGrid/Brevo)

---

## 📋 Checklist Final

### Configuración de Email:
- [ ] Variables de email agregadas en Railway
- [ ] Contraseña de aplicación creada (Gmail) o API Key (SendGrid)
- [ ] Email verificado (si usas SendGrid/Brevo)
- [ ] Servicio reiniciado después de cambios
- [ ] Logs verificados para confirmar configuración

### Gestión del Proyecto:
- [ ] Sé cómo pausar el servicio
- [ ] Sé cómo reanudar el servicio
- [ ] Sé cómo forzar un reinicio
- [ ] Tengo el enlace del proyecto guardado

---

## 🎯 URLs Importantes

- **Railway Dashboard:** https://railway.app/dashboard
- **Tu Proyecto:** https://railway.app/project/[tu-project-id]
- **URL de tu App:** https://springbootsabi-production.up.railway.app/

- **Gmail App Passwords:** https://myaccount.google.com/apppasswords
- **SendGrid:** https://app.sendgrid.com/
- **Brevo:** https://app.brevo.com/

---

## 💡 Consejos Finales

### Limites Gratuitos:
- **Gmail:** ~100-500 emails/día (no oficial)
- **SendGrid:** 100 emails/día
- **Brevo:** 300 emails/día

### Recomendación:
- **Para desarrollo/pruebas:** Gmail
- **Para producción:** SendGrid o Brevo

### Importante:
- **NO compartas** tu API Key o contraseña de aplicación
- **NO las subas** a GitHub
- Usa siempre variables de entorno en Railway

---

## 🆘 Si Algo Sale Mal

### El servicio no inicia:
1. Revisa los logs en Railway
2. Verifica las variables de base de datos (MYSQL*)
3. Asegúrate que `MYSQLPASSWORD` esté configurada

### Los emails no se envían:
1. Verifica los logs para errores de SMTP
2. Confirma que las credenciales sean correctas
3. Prueba con Gmail primero (es más simple)

### El proyecto consume muchos créditos:
1. Pausa los servicios que no uses
2. Reduce el uso de MySQL si no es necesario
3. Considera usar PostgreSQL (también gratis en Railway)

---

**¿Necesitas más ayuda?** Dime qué paso específico no te funciona y te ayudo más detalladamente. 🚀

