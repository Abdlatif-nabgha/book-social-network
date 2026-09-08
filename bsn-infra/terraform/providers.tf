terraform {
  required_version = ">= 1.15"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
     tls = {
    source  = "hashicorp/tls"
    version = "~> 4.0"
  }
  }
}

provider "aws" {
  region  = var.aws_region
  profile = "bsn"

  default_tags {
    tags = {
      Project     = "book-social-network"
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}