Set-ExecutionPolicy Bypass -Scope Process -Force;

Write-Host "Installing Apache Maven 3.6.3 ..." -ForegroundColor Cyan

$apachePath = "C:\apache-mavendir"
$mavenPath = "$apachePath\Maven"

if(Test-Path $mavenPath) {
    Remove-Item $mavenPath -Recurse -Force
}

if(-not (Test-Path $apachePath)) {
    New-Item $apachePath -ItemType directory -Force
}

Write-Host "Downloading..."
$zipPath = "$env:TEMP\apache-maven-3.6.3-bin.zip"
(New-Object Net.WebClient).DownloadFile('http://apache.mirror.globo.tech/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip', $zipPath)

Write-Host "Unpacking..."
Expand-Archive $zipPath -DestinationPath 'C:\apache-maven' | Out-Null
[IO.Directory]::Move('C:\apache-maven\apache-maven-3.6.3', $mavenPath)
Remove-Item 'C:\apache-maven' -Recurse -Force
del $zipPath


[Environment]::SetEnvironmentVariable("JAVA_HOME", "C:/Windows/TEMP/chocolatey/openjdk/jdk17", "User")
# [Environment]::SetEnvironmentVariable("Path", [System.Environment]::GetEnvironmentVariable('Path', [System.EnvironmentVariableTarget]::Machine) + ";$($env:JAVA_HOME)\bin")

[Environment]::SetEnvironmentVariable("M2_HOME", $mavenPath, "User")
[Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenPath, "User")

$env:Path += "$mavenPath\bin"

#mvn --version

Write-Host "Apache Maven 3.6.3 installed" -ForegroundColor Green