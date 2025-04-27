# Configure the Google Cloud Provider
provider "google" {
  project = "jerkins-test-453613"
  region  = "europe-west2"
}

resource "google_compute_network" "jenkins-vpc" {
  name                    = "jenkins-vpc"
  auto_create_subnetworks = false
}

# Create a subnet for the Jenkins VM
resource "google_compute_subnetwork" "jenkins-subnet" {
  name          = "jenkins-subnet"
  ip_cidr_range = "10.0.0.0/16"
  network       = google_compute_network.jenkins-vpc.id
}

# Create a firewall rule to allow HTTP and SSH traffic
resource "google_compute_firewall" "jenkins-firewall" {
  name    = "jenkins-firewall"
  network = google_compute_network.jenkins-vpc.id
  
  allow {
    protocol = "tcp"
    ports    = ["22", "8080"]
  }
  
  source_ranges = ["0.0.0.0/0"]
}

# Create a service account for the Jenkins VM
resource "google_service_account" "jenkins-sa" {
  account_id = "jenkins-sa"
}

# Create a key for the service account
resource "google_service_account_key" "jenkins-sa-key" {
  service_account_id = google_service_account.jenkins-sa.id
}

# Create a static IP address
resource "google_compute_address" "static_ip" {
  name   = "jenkins-static-ip"
  region = "europe-west2"
}

# Create a Compute Engine instance for Jenkins
resource "google_compute_instance" "final-version" {
  name         = "final-version"
  machine_type = "e2-medium"
  zone         = "europe-west2-a"
  allow_stopping_for_update = true
  
  boot_disk {
    initialize_params {
      image = "ubuntu-2204-lts"
    }
  }
  
  network_interface {
    subnetwork = google_compute_subnetwork.jenkins-subnet.id
    access_config {
      nat_ip = google_compute_address.static_ip.address
    }
  }
  
  service_account {
    email  = google_service_account.jenkins-sa.email
    scopes = ["cloud-platform"]
  }
  
  metadata = {
    startup-script = <<-EOF
      #!/bin/bash
      sudo apt-get update -y
      sudo apt-get install -y docker.io
      sudo systemctl start docker
      sudo systemctl enable docker
      sudo mkdir -p /var/jenkins_home
      sudo chown -R 1000:1000 /var/jenkins_home
      sudo docker run -d --name jenkins \
        -p 8080:8080 \
        -p 50000:50000 \
        -v /var/jenkins_home:/var/jenkins_home \
        jenkins/jenkins:lts
    EOF
  }
}

output "external_ip" {
  value = google_compute_instance.final-version.network_interface[0].access_config[0].nat_ip
}
