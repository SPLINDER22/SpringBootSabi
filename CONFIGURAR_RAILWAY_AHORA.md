# 🚨 SOLUCIÓN URGENTE - Configurar Variables de Railway

## ❌ PROBLEMA ACTUAL

La aplicación muestra:
```
Original DATABASE_URL: postgresql://user:****@host:port/database
```

Esto significa que Railway tiene valores **literales** en lugar de las credenciales **reales** de PostgreSQL.

---

## ✅ SOLUCIÓN PASO A PASO

### 1️⃣ IR A RAILWAY DASHBOARD

1. Abre: https://railway.app/
2. Ve a tu proyecto **SABI**
3. Deberías ver **2 servicios**:
   - 🐘 **Postgres** (Base de datos)
   - ☕ **sabi** (Tu aplicación Spring Boot)

---

### 2️⃣ OBTENER LAS VARIABLES DE POSTGRESQL

1. Haz clic en el servicio **Postgres**
2. Ve a la pestaña **"Variables"** o **"Connect"**
3. Copia TODAS estas variables (están generadas automáticamente):

```bash
PGHOST=xxxxxxx.railway.app
PGPORT=5432 (o el puerto asignado)
PGDATABASE=railway
PGUSER=postgres
PGPASSWORD=xxxxxxxxxxxxxx (contraseña generada)
DATABASE_URL=postgresql://postgres:password@host.railway.app:port/railway
```

---

### 3️⃣ CONFIGURAR EN TU APLICACIÓN (SABI)

#### Opción A: Usar Referencias (RECOMENDADO)

1. Haz clic en el servicio **sabi** (tu aplicación)
2. Ve a **"Variables"**
3. **ELIMINA** cualquier variable con valores literales como:
   - ❌ `DATABASE_URL=postgresql://user:password@host:port/database`
   
4. Agrega las variables usando **Referencias**:
   - Haz clic en **"+ New Variable"**
   - Haz clic en **"Add Reference"**
   - Selecciona: **Postgres** como servicio
   - Agrega estas referencias:

```bash
PGHOST=${{Postgres.PGHOST}}
PGPORT=${{Postgres.PGPORT}}
PGDATABASE=${{Postgres.PGDATABASE}}
PGUSER=${{Postgres.PGUSER}}
PGPASSWORD=${{Postgres.PGPASSWORD}}
```

#### Opción B: Copiar Valores Manualmente

Si las referencias no funcionan, copia los valores reales del servicio Postgres:

1. Ve al servicio **Postgres** → **Variables**
2. Copia cada valor
3. Ve al servicio **sabi** → **Variables**
4. Pega los valores **EXACTOS**:

```bash
PGHOST=containers-us-west-xxx.railway.app (el tuyo)
PGPORT=5432
PGDATABASE=railway
PGUSER=postgres
PGPASSWORD=tu_contraseña_real_generada_por_railway
```

---

### 4️⃣ VERIFICAR CONFIGURACIÓN

Después de agregar las variables:

1. **NO** dejes ninguna variable con valores como `user`, `password`, `host`, `port`, `database`
2. Railway debería **redesplegar** automáticamente
3. Si no lo hace, haz clic en **"Deploy"** → **"Redeploy"**

---

### 5️⃣ VERIFICAR LOGS

Una vez redesplegado:

1. Ve a **Deployments** → último deployment
2. Abre los **Logs**
3. Deberías ver:

```
✅ PGHOST: containers-us-west-xxx.railway.app
✅ PGPORT: 5432
✅ PGDATABASE: railway
✅ PGUSER: postgres
✅ PGPASSWORD: ******
✅ JDBC URL created: jdbc:postgresql://...
✅ DataSource configured successfully!
```

---

## 🎯 CÓMO SE VE LA CONFIGURACIÓN CORRECTA

### ✅ CORRECTO (Variables del Servicio Postgres):

```bash
# En el servicio Postgres
PGHOST=containers-us-west-185.railway.app
PGPORT=7237
PGDATABASE=railway
PGUSER=postgres
PGPASSWORD=aBcDeFgHiJkLmNoPqRsTuVwXyZ123456
DATABASE_URL=postgresql://postgres:aBcDeFgHiJkLmNoPqRsTuVwXyZ123456@containers-us-west-185.railway.app:7237/railway
```

### ✅ CORRECTO (Variables en tu aplicación SABI):

```bash
# Usando referencias
PGHOST=${{Postgres.PGHOST}}
PGPORT=${{Postgres.PGPORT}}
PGDATABASE=${{Postgres.PGDATABASE}}
PGUSER=${{Postgres.PGUSER}}
PGPASSWORD=${{Postgres.PGPASSWORD}}
```

O

```bash
# Valores copiados manualmente
PGHOST=containers-us-west-185.railway.app
PGPORT=7237
PGDATABASE=railway
PGUSER=postgres
PGPASSWORD=aBcDeFgHiJkLmNoPqRsTuVwXyZ123456
```

### ❌ INCORRECTO (Lo que tienes ahora):

```bash
DATABASE_URL=postgresql://user:password@host:port/database
```

---

## 🔧 SI USAS GITHUB

Si estás usando GitHub para desplegar:

1. **NO** pongas las credenciales en el repositorio
2. Configura las variables **solo en Railway Dashboard**
3. Las variables de entorno de Railway sobrescribirán cualquier configuración

---

## 📱 ALTERNATIVA: USAR RAILWAY CLI

Si tienes Railway CLI instalado:

```bash
# Conectar variables del Postgres al servicio sabi
railway link

# Ver variables actuales
railway variables

# Agregar variables (reemplaza con tus valores reales)
railway variables set PGHOST=containers-us-west-xxx.railway.app
railway variables set PGPORT=5432
railway variables set PGDATABASE=railway
railway variables set PGUSER=postgres
railway variables set PGPASSWORD=tu_contraseña_real
```

---

## ⚠️ IMPORTANTE

1. **NUNCA** uses valores literales como `user`, `password`, `host`, `port`, `database`
2. Railway genera automáticamente las credenciales de PostgreSQL
3. Las variables deben estar en **ambos servicios** o usar **referencias**
4. Después de cambiar variables, Railway **redesplega automáticamente**

---

## 🎉 CUANDO FUNCIONE VERÁS:

```
✅ DataSource configured successfully!
✅ Application started on port 8080
✅ Tomcat started successfully
```

Y al visitar la URL de Railway verás tu aplicación funcionando.

---

## 📸 CAPTURAS RECOMENDADAS

Si sigues teniendo problemas, envíame capturas de:

1. Railway Dashboard → Servicio **Postgres** → Pestaña **Variables**
2. Railway Dashboard → Servicio **sabi** → Pestaña **Variables**
3. Logs del último deployment

---

**¿Todo claro?** Sigue estos pasos y la aplicación funcionará. 🚀

