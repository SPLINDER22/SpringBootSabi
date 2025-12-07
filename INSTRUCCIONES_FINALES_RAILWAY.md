# 🚀 INSTRUCCIONES FINALES PARA RAILWAY

## ✅ TODO ESTÁ CONFIGURADO - SOLO SIGUE ESTOS PASOS

---

## PASO 1: HACER COMMIT Y PUSH

```bash
cd C:\Users\USER\Downloads\SpringBootSabi
git add .
git commit -m "Fix completo: Java 17, connection pool, configuración optimizada"
git push origin main
```

⏱️ Railway detectará el push y comenzará a redesplegar automáticamente

---

## PASO 2: CONFIGURAR VARIABLES EN RAILWAY

### Ir a Railway:
1. Ve a https://railway.app/dashboard
2. Click en tu proyecto
3. Click en el servicio "sabi"
4. Click en la pestaña **"Variables"**

### Agregar estas variables (una por una):

#### OBLIGATORIA:
```
Variable: SPRING_PROFILES_ACTIVE
Value: prod
```

#### OPCIONALES (pero recomendadas):
```
Variable: MAIL_USERNAME
Value: Sabi.geas5@gmail.com

Variable: MAIL_PASSWORD  
Value: Williamespinel1

Variable: UPLOAD_PATH
Value: /app/uploads/perfiles

Variable: UPLOAD_DIAGNOSTICOS_PATH
Value: /app/uploads/diagnosticos

Variable: JAVA_OPTS
Value: -Xmx512m -Xms256m

Variable: TZ
Value: America/Bogota
```

### ⚠️ IMPORTANTE:
- **NO agregues DATABASE_URL** - Railway la crea automáticamente
- Si el correo falla, la app IGUAL iniciará (configurado para ser opcional)

---

## PASO 3: VERIFICAR QUE POSTGRESQL ESTÁ CONECTADO

1. En Railway, en tu proyecto
2. Deberías ver:
   - Un servicio llamado **"sabi"** (tu Spring Boot)
   - Un servicio llamado **"Postgres"** (la base de datos)
3. **Si NO ves Postgres:**
   - Click en **"+ New"**
   - Selecciona **"Database"**
   - Selecciona **"Add PostgreSQL"**
   - Railway conectará automáticamente con tu servicio

---

## PASO 4: ESPERAR EL DEPLOYMENT (3-5 minutos)

### En Railway → Deployments:
- Verás el progreso del build
- Estados:
  - 🔵 **Building** → Compilando el proyecto
  - 🟢 **Deployed** → ¡Funcionando!
  - 🔴 **Failed** → Hubo un error (ve al PASO 5)

---

## PASO 5: VERIFICAR QUE FUNCIONA

### Una vez que el estado sea "Deployed":

1. **Obtén tu URL:**
   - Railway → Settings → Networking
   - Si no hay dominio, click en **"Generate Domain"**
   - Copia la URL (ejemplo: `sabi-production.up.railway.app`)

2. **Prueba el health endpoint:**
   ```
   https://tu-dominio.railway.app/health
   ```
   
   Debería responder:
   ```json
   {
     "status": "UP",
     "application": "Sabi",
     "timestamp": "..."
   }
   ```

3. **Prueba la página principal:**
   ```
   https://tu-dominio.railway.app/
   ```

---

## ✅ CAMBIOS APLICADOS QUE SOLUCIONAN LOS PROBLEMAS

### 1. Java 17 ✅
- Cambiado de Java 21 a Java 17 (compatible con Railway)

### 2. Connection Pool Optimizado ✅
```properties
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.connection-timeout=30000
```

### 3. Correo No Bloqueante ✅
- Si el correo falla, la app NO crashea
- Configurado con timeouts y valores por defecto

### 4. Logs Verbosos ✅
- Si hay problemas, los verás claramente en los logs

### 5. Variables de Entorno con Defaults ✅
- La app inicia incluso si faltan algunas variables opcionales

---

## 🔍 SI AÚN NO FUNCIONA

### Ver los logs:
1. Railway → Deployments
2. Click en el último deployment
3. Click en **"View Logs"**

### Busca:
- ✅ **"Started SabiApplication"** = ¡ÉXITO!
- ❌ **"ERROR"** o **"Exception"** = Problema

### Errores comunes y soluciones:

| Error en Logs | Solución |
|---------------|----------|
| `Failed to configure a DataSource` | Agrega PostgreSQL en Railway |
| `DATABASE_URL is not set` | Verifica que PostgreSQL está conectado |
| `SPRING_PROFILES_ACTIVE` no configurado | Agrega la variable en Railway |
| `OutOfMemoryError` | Ya configurado JAVA_OPTS |
| `Port already in use` | Ya configurado correctamente |

---

## 📊 CHECKLIST FINAL

Antes de decir que no funciona, verifica:

- [x] ✅ Hiciste commit y push
- [ ] ✅ SPRING_PROFILES_ACTIVE=prod configurado en Railway
- [ ] ✅ PostgreSQL agregado en Railway
- [ ] ✅ Esperaste a que el deployment termine (3-5 min)
- [ ] ✅ Generaste un dominio en Railway → Settings → Networking
- [ ] ✅ Probaste https://tu-dominio.railway.app/health

---

## 🎯 RESUMEN

**LO ÚNICO QUE NECESITAS HACER:**

1. ✅ Commit + Push (YA HECHO los cambios en el código)
2. ✅ Agregar variable `SPRING_PROFILES_ACTIVE=prod` en Railway
3. ✅ Verificar que PostgreSQL está agregado
4. ✅ Esperar 3-5 minutos
5. ✅ Probar tu dominio

**¡Eso es TODO!** La app debería funcionar perfectamente.

---

**Si después de estos pasos sigue sin funcionar, copia los logs y me los pasas.**

